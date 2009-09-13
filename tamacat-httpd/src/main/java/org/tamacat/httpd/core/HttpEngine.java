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
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.jmx.BasicCounter;
import org.tamacat.httpd.jmx.BasicHttpMonitor;
import org.tamacat.httpd.ssl.SSLContextCreator;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is implements of the multi thread server.
 */
public class HttpEngine implements BasicHttpMonitor {

	static final Log LOG = LogFactory.getLog(HttpEngine.class);

	private ServerConfig serverConfig;
	private HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
	private DefaultHttpService service;
	
	private SSLContextCreator sslContextCreator;
    private ServerSocket serversocket;
    private HttpParamsBuilder paramsBuilder;

    private boolean isInitalized;
    private ExecutorService executors;
    
    private static BasicCounter counter = new BasicCounter();
    private List<HttpResponseInterceptor> interceptors
    	= new ArrayList<HttpResponseInterceptor>();
    
    /**
     * <p>This method called by {@link #start}.
     */
	protected void init() {
		if (isInitalized == false) {
			serverConfig = new ServerConfig();
	        paramsBuilder = new HttpParamsBuilder();
	        paramsBuilder.socketTimeout(serverConfig.getSocketTimeout())
	          .socketBufferSize(serverConfig.getSocketBufferSize());
			try {
				int port = serverConfig.getPort();
				if (serverConfig.useHttps()) {					
					serversocket = createSecureServerSocket(port);
				} else {
					serversocket = new ServerSocket(port);
				}
				service = new DefaultHttpService();
				for (HttpResponseInterceptor interceptor : interceptors) {
					service.setHttpResponseInterceptor(interceptor);
				}
				registryMXServer();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			isInitalized = true;
		}
	}

	/**
	 * <p>Start the http server.
	 */
	public void start() {
		//Initalize engine.
		init();
		
		//Register services and service URLs.
		ServiceConfig serviceConfig
			= new ServiceConfigXmlParser(serverConfig).getServiceConfig();
		for (ServiceUrl serviceUrl : serviceConfig.getServiceUrlList()) {
			registry.register(serviceUrl.getPath() + "*", serviceUrl.getHttpHandler());
		}
        service.setHandlerResolver(registry);
        executors = new ThreadExecutorFactory(serverConfig).getExecutorService();
		LOG.info("Listen: " + serverConfig.getPort());
        while (!Thread.interrupted()) {
            try {
                //socket accept -> execute WorkerThrad.
                Socket insocket = serversocket.accept();
                
                executors.execute(new WorkerThread(
                	service, insocket, paramsBuilder.buildParams(), counter)
                );
            } catch (InterruptedIOException e) {
            	LOG.error(e.getMessage());
                break;
            } catch (IOException e) {
            	LOG.error(e.getMessage());
            } catch (Exception e) {
            	LOG.error(e.getMessage(), e);
            }
        }
        executors.shutdown();
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
}