/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.apache.http.nio.protocol.NHttpRequestHandlerRegistry;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.tamacat.httpd.config.HostServiceConfig;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.HttpParamsBuilder;
import org.tamacat.httpd.core.HttpProcessorBuilder;
import org.tamacat.httpd.jmx.BasicCounter;
import org.tamacat.httpd.jmx.BasicHttpMonitor;
import org.tamacat.httpd.ssl.SSLContextCreator;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is implements of the multi thread server.
 */
public class NHttpEngine implements BasicHttpMonitor, Runnable {

	static final Log LOG = LogFactory.getLog(NHttpEngine.class);

	private String propertiesName = "server.properties";
	private ServerConfig serverConfig;
	private DefaultAsyncNHttpServiceHandler service;
	
	private SSLContextCreator sslContextCreator;
//    private ServerSocket serversocket;
    private HttpParamsBuilder paramsBuilder;
    HttpProcessorBuilder procBuilder;
	private boolean isMXServerStarted;

    private static BasicCounter counter = new BasicCounter();
    private List<HttpResponseInterceptor> interceptors
    	= new ArrayList<HttpResponseInterceptor>();
    
    /**
     * <p>This method called by {@link #start}.
     */
	protected void init() {
		if (serverConfig == null) {
			Properties props = PropertyUtils.getProperties(propertiesName);
			serverConfig = new ServerConfig(props);
			paramsBuilder = new HttpParamsBuilder();
	        paramsBuilder.socketTimeout(serverConfig.getSocketTimeout())
	          .socketBufferSize(serverConfig.getSocketBufferSize())
	          .originServer(serverConfig.getParam("ServerName"));
	        procBuilder = new HttpProcessorBuilder();
		}
		//default interceptors
		procBuilder.addInterceptor(new ResponseDate());
		procBuilder.addInterceptor(new ResponseServer());
		procBuilder.addInterceptor(new ResponseContent());
		procBuilder.addInterceptor(new ResponseConnControl());
		
		//add interceptors
		for (HttpResponseInterceptor interceptor : interceptors) {
			procBuilder.addInterceptor(interceptor);
		}
		
		String componentsXML = serverConfig.getParam(
				"components.file", "components.xml");
		NHttpHandlerFactory factory = new DefaultNHttpHandlerFactory(
				componentsXML, getClass().getClassLoader());

		service = new DefaultAsyncNHttpServiceHandler(
				procBuilder.build(), paramsBuilder.buildParams());
		if (isMXServerStarted == false) {
			registryMXServer();
		}
		//Register services and service URLs.
		HostServiceConfig hostConfig = new ServiceConfigParser(serverConfig).getConfig();
		for (String host : hostConfig.getHosts()) {
			NHttpRequestHandlerRegistry registry = new NHttpRequestHandlerRegistry();
			ServiceConfig serviceConfig = hostConfig.getServiceConfig(host);
			for (ServiceUrl serviceUrl : serviceConfig.getServiceUrlList()) {
				NHttpRequestHandler handler = factory.getNHttpRequestHandler(serviceUrl, serviceUrl.getHandlerName());
				if (handler != null) {
					LOG.info(serviceUrl.getPath() + " - " + handler.getClass().getName());
					registry.register(serviceUrl.getPath() + "*", handler);
				} else {
					LOG.warn(serviceUrl.getPath() + " HttpHandler is not found.");
				}
			}
			service.setHandlerResolver(registry);
		}
	}

	/**
	 * <p>Start the http server.
	 */
	public void startHttpd() {
		//Initalize engine.
		init();
        
        IOEventDispatch ioEventDispatch
        	= new DefaultIoEventDispatch(service, paramsBuilder.buildParams());
        	//= new DefaultServerIOEventDispatch(service, paramsBuilder.buildParams());

        //service.setEventListener(new LoggingEventListener());
        
        try {
	        ListeningIOReactor listeningIOReactor
	        	= new DefaultListeningIOReactor(serverConfig.getMaxThreads(), paramsBuilder.buildParams());

	        listeningIOReactor.listen(new InetSocketAddress(serverConfig.getPort()));
			LOG.info("Listen: " + serverConfig.getPort());
	        listeningIOReactor.execute(ioEventDispatch);
	
        }  catch (InterruptedIOException e) {
        	counter.error();
        	LOG.error(e.getMessage());
        } catch (IOException e) {
        	counter.error();
        	LOG.error(e.getMessage(), e.getCause());
        } catch (Exception e) {
        	counter.error();
        	LOG.error(e.getMessage(), e);
        }
	}
	
	@Override
	public void run() {
		startHttpd();
	}
	
	public String getPropertiesName() {
		return propertiesName;
	}

	public void setPropertiesName(String propertiesName) {
		this.propertiesName = propertiesName;
	}

	/**
	 * <p>Set the {@link SSLContextCreator},
	 * when customize the configration of https (SSL/TSL).
	 * When I did not set it, it is generated by a {@code createSecureServerSocket}.
	 * @param sslContextCreator
	 */
	public void setSSLContextCreator(SSLContextCreator sslContextCreator) {
		this.sslContextCreator = sslContextCreator;
	}
	
	/**
	 * <p>Create the secure {@link ServerSocket}.
	 * @param port HTTPS listen port.
	 * @return created the {@link ServerSocket} 
	 * @throws IOException
	 */
	protected ServerSocket createSecureServerSocket(int port) throws IOException {
		if (sslContextCreator == null) {
			sslContextCreator = new SSLContextCreator(serverConfig);
		}
		SSLContext ctx = sslContextCreator.getSSLContext();
        return ctx.getServerSocketFactory().createServerSocket(port);
	}
	
	/**
	 * <p>Add the response interceptor.
	 * @param interceptor
	 * @since 0.5
	 */
	public void setHttpResponseInterceptor(HttpResponseInterceptor interceptor) {
		interceptors.add(interceptor);
	}
	
	//install
	//http://ws-jmx-connector.dev.java.net/files/documents/4956/114781/jsr262-ri.jar
	//https://jax-ws.dev.java.net/2.1.1/JAXWS2.1.1_20070501.jar
	void registryMXServer() {
		try {
			//"service:jmx:rmi:///jndi/rmi://localhost/httpd";
			//"ws", "localhost", 9999, "/admin"
			String jmxUrl = serverConfig.getParam("JMX.server-url");
			if (StringUtils.isNotEmpty(jmxUrl)) {
				String objectName = serverConfig.getParam(
						"JMX.objectname","org.tamacat.httpd:type=HttpEngine");
				int rmiPort = serverConfig.getParam("JMX.rmi.port", -1);
				if (rmiPort > 0) {
					LocateRegistry.createRegistry(rmiPort);
				}
				MBeanServer server = ManagementFactory.getPlatformMBeanServer(); 
	        	ObjectName name = new ObjectName(objectName);
	        	server.registerMBean(this, name);
	        	
	        	JMXConnectorServer sv = JMXConnectorServerFactory.newJMXConnectorServer(
	                new JMXServiceURL(jmxUrl), null, server);
	        	sv.start();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.trace(ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public int getActiveConnections() {
		return counter.getActiveConnections();
	}

	@Override
	public long getAccessCount() {
		return counter.getAccessCount();
	}

	@Override
	public long getErrorCount() {
		return counter.getErrorCount();
	}

	@Override
	public Date getStartedTime() {
		return counter.getStartedTime();
	}

	@Override
	public void resetAccessCount() {
		counter.resetAccessCount();
	}

	@Override
	public void resetErrorCount() {
		counter.resetErrorCount();
    }
}