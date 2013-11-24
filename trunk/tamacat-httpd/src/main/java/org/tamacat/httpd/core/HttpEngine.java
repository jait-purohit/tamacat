/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.jmx.BasicCounter;
import org.tamacat.httpd.core.jmx.JMXReloadableHttpd;
import org.tamacat.httpd.core.ssl.SSLContextCreator;
import org.tamacat.httpd.handler.DefaultHttpService;
import org.tamacat.httpd.handler.HostRequestHandlerMapper;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is implements of the multi-thread server.
 */
public class HttpEngine implements JMXReloadableHttpd, Runnable {

	static final Log LOG = LogFactory.getLog(HttpEngine.class);

	private String propertiesName = "server.properties";

	private ServerConfig serverConfig;
	private ObjectName objectName;

	private SSLContextCreator sslContextCreator;
	private ServerSocket serverSocket;
	private WorkerExecutor workerExecutor = new DefaultWorkerExecutor();

	private BasicCounter counter = new BasicCounter();

	private List<HttpRequestInterceptor> requestInterceptors = new ArrayList<>();

	private List<HttpResponseInterceptor> responseInterceptors = new ArrayList<>();

	private static JMXConnectorServer jmxServer;
	private static Registry rmiRegistry;
	private boolean isMXServerStarted;
	private ClassLoader loader;

	/**
	 * <p>Start the http server.
	 */
	@Override
	public void startHttpd() {
		//Initalize engine.
		init();

		LOG.info("Listen: " + serverConfig.getPort());
		serverSocket = createServerSocket();
		while (!Thread.interrupted()) {
			try {
				workerExecutor.execute(serverSocket.accept());
			} catch (InterruptedIOException e) {
				counter.error();
				LOG.error(e.getMessage());
				break;
			} catch (IOException e) {
				counter.error();
				LOG.error(e.getMessage());
				if (serverSocket.isClosed()) { //for stop()
					break;
				}
			} catch (Exception e) {
				counter.error();
				LOG.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void stopHttpd() {
		try {
			if (serverSocket != null) serverSocket.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (workerExecutor != null) workerExecutor.shutdown();
		}
	}

	@Override
	public void restartHttpd() {
		for (;;) {
			if (counter.getActiveConnections() == 0) {
				stopHttpd();
				startHttpd();
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
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
	 * Set the WorkerExecutor.
	 * @param WorkerExecutor
	 * @sinse 1.1
	 */
	public void setWorkerExecutor(WorkerExecutor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	/**
	 * <p>Add the request interceptor.
	 * @param interceptor
	 * @since 0.9
	 */
	public void setHttpRequestInterceptor(HttpRequestInterceptor interceptor) {
		requestInterceptors.add(interceptor);
	}

	/**
	 * <p>Add the response interceptor.
	 * @param interceptor
	 * @since 0.5
	 */
	public void setHttpResponseInterceptor(HttpResponseInterceptor interceptor) {
		responseInterceptors.add(interceptor);
	}

	/**
	 * <p>This method called by {@link #start}.
	 */
	protected void init() {
		if (serverConfig == null) {
			Properties props = PropertyUtils.getProperties(propertiesName);
			serverConfig = new ServerConfig(props);
		}
		workerExecutor.setServerConfig(serverConfig);

		//org.apache.http.config.
		//SocketConfig socketConfig = SocketConfig.custom()
		//	.setSoTimeout(serverConfig.getSocketTimeout()).build();

		//paramsBuilder = new HttpParamsBuilder();
		//paramsBuilder.socketTimeout(serverConfig.getSocketTimeout())
		//	.socketBufferSize(serverConfig.getSocketBufferSize())
		//	.originServer(serverConfig.getParam("ServerName"));
		HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();
		//default interceptors
		procBuilder.addInterceptor(new ResponseDate());
		procBuilder.addInterceptor(new ResponseServer());
		procBuilder.addInterceptor(new ResponseContent());
		procBuilder.addInterceptor(new ResponseConnControl());

		//add interceptors
		for (HttpRequestInterceptor interceptor : requestInterceptors) {
			procBuilder.addInterceptor(interceptor);
		}
		for (HttpResponseInterceptor interceptor : responseInterceptors) {
			procBuilder.addInterceptor(interceptor);
		}
		DefaultHttpService service = new DefaultHttpService(
			procBuilder, new KeepAliveConnReuseStrategy(serverConfig),
			new DefaultHttpResponseFactory(), null, null);

		if (isMXServerStarted == false) {
			registerMXServer();
		}

		String componentsXML = serverConfig.getParam("components.file", "components.xml");
		HostRequestHandlerMapper hostResolver = new HostRequestHandlerMapper().create(serverConfig, componentsXML);
		service.setHostHandlerResolver(hostResolver);
		workerExecutor.setHttpService(service);
	}

	protected ServerSocket createServerSocket() {
		ServerSocket serverSocket = null;
		try {
			//setup the server port.
			int port = serverConfig.getPort();
			if (serverConfig.useHttps()) {
				serverSocket = createSecureServerSocket(port);
				if (serverConfig.useClientAuth() && serverSocket instanceof SSLServerSocket) {
					((SSLServerSocket)serverSocket).setNeedClientAuth(true);
				}
			} else {
				serverSocket = new ServerSocket(port);
			}
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		return serverSocket;
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

	//install
	//http://ws-jmx-connector.dev.java.net/files/documents/4956/114781/jsr262-ri.jar
	//https://jax-ws.dev.java.net/2.1.1/JAXWS2.1.1_20070501.jar
	public void registerMXServer() {
		try {
			//"service:jmx:rmi:///jndi/rmi://localhost/httpd";
			//"ws", "localhost", 9999, "/admin"
			String jmxUrl = serverConfig.getParam("JMX.server-url");
			if (!isMXServerStarted && StringUtils.isNotEmpty(jmxUrl)) {
				String name = serverConfig.getParam(
						"JMX.objectname","org.tamacat.httpd:type=HttpEngine");

				int rmiPort = serverConfig.getParam("JMX.rmi.port", -1);
				if (rmiPort > 0) {
					rmiRegistry = LocateRegistry.createRegistry(rmiPort);
				}
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				objectName = new ObjectName(name);
				server.registerMBean(this, objectName);

				HashMap<String,Object> env = new HashMap<>();
				//env.put("jmx.remote.x.password.file", serverConfig.getParam("JMX.password", ""));
				//env.put("jmx.remote.x.access.file", serverConfig.getParam("JMX.access", ""));
				jmxServer = JMXConnectorServerFactory.newJMXConnectorServer(
					new JMXServiceURL(jmxUrl), env, server);
				jmxServer.start();
				isMXServerStarted = true;
			}
			if (isMXServerStarted) counter.register();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug(ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public void unregisterMXServer() {
		if (isMXServerStarted) {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			try {
				counter.unregister();
				server.unregisterMBean(objectName);
				if (jmxServer != null) jmxServer.stop();
				if (rmiRegistry != null) UnicastRemoteObject.unexportObject(rmiRegistry, true);
				isMXServerStarted = false;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.warn(ExceptionUtils.getStackTrace(e));
			}
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

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public void setClassLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public ClassLoader getClassLoader() {
		if (loader == null) return Thread.currentThread().getContextClassLoader();
		else return loader;
	}
}