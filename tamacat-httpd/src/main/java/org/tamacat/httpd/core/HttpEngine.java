/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;

import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.ssl.SSLContextCreator;

public class HttpEngine {

	static final Log LOG = LogFactory.getLog(HttpEngine.class);

	private ServerConfig serverConfig;
	private HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
	private HttpService service;
	
	private SSLContextCreator sslContextCreator;
    private ServerSocket serversocket;
    private HttpParamsBuilder paramsBuilder;

    private boolean isInitalized;
    protected ExecutorService executors;
    
    public HttpEngine() {}
    
	public void init() {
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
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			isInitalized = true;
		}
	}

	public void start() {
		//Initalize engine.
		init();
		
		//Register services and service URLs.
		ServiceConfig serviceConfig
			= new ServiceConfigXmlParser(serverConfig).getReverseConfig();
		for (ServiceUrl serviceUrl : serviceConfig.getServiceUrlList()) {
			registry.register(serviceUrl.getPath() + "*", serviceUrl.getHttpHandler());
		}
        service.setHandlerResolver(registry);
        executors = new ThreadExecutorFactory(serverConfig).getExecutorService();
		
        while (!Thread.interrupted()) {
            try {
                //socket accept -> execute WorkerThrad.
                Socket insocket = serversocket.accept();
                executors.execute(
                		new WorkerThread(service, insocket, paramsBuilder.buildParams()));
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
	
	public void setSSLContextCreator(SSLContextCreator sslContextCreator) {
		this.sslContextCreator = sslContextCreator;
	}
	
	protected ServerSocket createSecureServerSocket(int port) throws IOException {
		if (sslContextCreator == null) {
			sslContextCreator = new SSLContextCreator(serverConfig);
		}
		SSLContext ctx = sslContextCreator.getSSLContext();
        return ctx.getServerSocketFactory().createServerSocket(port);
	}
}
