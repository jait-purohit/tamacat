/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.config.VirtualHostConfig;
import org.tamacat.httpd.config.VirtualHostConfigXmlParser;
import org.tamacat.httpd.jmx.BasicCounter;
import org.tamacat.httpd.jmx.JMXReloadableHttpd;
import org.tamacat.httpd.ssl.SSLContextCreator;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is implements of the multi thread server.
 */
public class HttpEngine implements JMXReloadableHttpd {

	static final Log LOG = LogFactory.getLog(HttpEngine.class);

	private ServerConfig serverConfig;
	private DefaultHttpService service;
	
	private SSLContextCreator sslContextCreator;
    private ServerSocket serversocket;
    private HttpParamsBuilder paramsBuilder;

    private ExecutorService executors;
    
    private BasicCounter counter = new BasicCounter();
    private List<HttpResponseInterceptor> interceptors
    	= new ArrayList<HttpResponseInterceptor>();
    
	private boolean isMXServerStarted;

    /**
     * <p>This method called by {@link #start}.
     */
	protected void init() {
		if (serverConfig == null) {
			serverConfig = new ServerConfig();
			paramsBuilder = new HttpParamsBuilder();
	        paramsBuilder.socketTimeout(serverConfig.getSocketTimeout())
	          .socketBufferSize(serverConfig.getSocketBufferSize())
	          .originServer(serverConfig.getParam("ServerName"));
		}
		service = new DefaultHttpService();
		if (isMXServerStarted == false) {
			registryMXServer();
		}
		for (HttpResponseInterceptor interceptor : interceptors) {
			service.setHttpResponseInterceptor(interceptor);
		}
		HttpHandlerFactory factory = new DefaultHttpHandlerFactory();

		HostRequestHandlerResolver hostResolver = new HostRequestHandlerResolver();
		VirtualHostConfig hostConfig = new VirtualHostConfigXmlParser(serverConfig).getVirtualHostConfig();
		for (String host : hostConfig.getHosts()) {
			HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
			ServiceConfig serviceConfig = hostConfig.getServiceConfig(host);
			for (ServiceUrl serviceUrl : serviceConfig.getServiceUrlList()) {
				HttpHandler handler = factory.getHttpHandler(serviceUrl);
				if (handler != null) {
					LOG.info(serviceUrl.getPath() + " - " + handler.getClass().getName());
					registry.register(serviceUrl.getPath() + "*", handler);
				} else {
					LOG.warn(serviceUrl.getPath() + " HttpHandler is not found.");
				}
			}
			hostResolver.setHostRequestHandlerResolver(host, registry);
		}
        service.setHostHandlerResolver(hostResolver);
	}

	/**
	 * <p>Start the http server.
	 */
	@Override
	public void start() {
		//Initalize engine.
		init();
		
		try {
			//setup the server port. 
			int port = serverConfig.getPort();
			if (serverConfig.useHttps()) {					
				serversocket = createSecureServerSocket(port);
			} else {
				serversocket = new ServerSocket(port);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//set the maximun worker threads.
		int maxThreads = serverConfig.getMaxThreads();
		LOG.info("MaxServerThreads: " + maxThreads);
		String threadName = serverConfig.getParam("WorkerThreadName", "httpd");
		//create the ExecutorService.
		executors = new ThreadExecutorFactory(threadName).getExecutorService(maxThreads);

		LOG.info("Listen: " + serverConfig.getPort());
        while (!Thread.interrupted()) {
            try {
                //socket accept -> execute WorkerThrad.
                Socket insocket = serversocket.accept();
                executors.execute(new WorkerThread(
                	service, insocket, paramsBuilder.buildParams(), counter)
                );
            } catch (InterruptedIOException e) {
            	counter.error();
            	LOG.error(e.getMessage());
                break;
            } catch (IOException e) {
            	counter.error();
            	LOG.error(e.getMessage());
            	if (serversocket.isClosed()) { //for stop()
            		break;
            	}
            } catch (Exception e) {
            	counter.error();
            	LOG.error(e.getMessage(), e);
            }
        }
        executors.shutdown();
	}
	
	@Override
	public void stop() {
		try {
			serversocket.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			executors.shutdown();
		}
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
	        	isMXServerStarted = true;
			}
			counter.register();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.warn(ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public void reload() {
		init();
		LOG.info("reloaded.");
	}

	@Override
	public int getMaxServerThreads() {
		return serverConfig.getMaxThreads();
	}

	@Override
	public void setMaxServerThreads(int max) {
		serverConfig.setParam("MaxServerThreads",String.valueOf(max));
	}
}