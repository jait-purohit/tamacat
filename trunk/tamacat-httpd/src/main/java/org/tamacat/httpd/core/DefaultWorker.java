/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpRequestFactory;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.Worker;
import org.tamacat.httpd.core.jmx.PerformanceCounter;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class DefaultWorker implements Worker {
	static final Log LOG = LogFactory.getLog(DefaultWorker.class);
	static final DiagnosticContext DC = LogFactory.getDiagnosticContext(LOG);

	static final String HTTP_IN_CONN = "http.in-conn";
	static final String HTTP_OUT_CONN = "http.out-conn";

	protected ServerConfig serverConfig;
	protected HttpService httpService;
	protected Socket socket;
	protected PerformanceCounter counter;
	protected ServerHttpConnection conn;
	protected HttpRequestFactory httpRequestFactory;

	public DefaultWorker() {
		httpRequestFactory = new StandardHttpRequestFactory();
	}

	public DefaultWorker(ServerConfig serverConfig, HttpService httpService, HttpRequestFactory httpRequestFactory,Socket socket) {
		this.httpRequestFactory = httpRequestFactory;
		setServerConfig(serverConfig);
		setHttpService(httpService);
		setSocket(socket);
	}

	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.conn = new ServerHttpConnection(serverConfig.getSocketBufferSize(), httpRequestFactory);
		//serverConfig.getParam("WorkerThreadName", "httpd");
	}

	@Override
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	@Override
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void setPerformanceCounter(PerformanceCounter counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		HttpContext parent = new BasicHttpContext(); //for Keep-Alive timeout
		// Bind connection objects to the execution context
		parent.setAttribute(HTTP_IN_CONN, conn);
		try {
			countUp();
			this.conn.bind(socket);
			LOG.debug("start worker - " + conn);

			HttpConnectionMetrics metrics = this.conn.getMetrics();
			while (Thread.interrupted() == false && conn.isOpen()) {
				LOG.debug("count:" + metrics.getRequestCount() + " - " + conn);
				HttpContext context = new BasicHttpContext(parent);
				this.httpService.handleRequest(conn, context);

				// close client connection. (unsupported keep-alive)
				HttpConnection clientConn = (HttpConnection) context.getAttribute(HTTP_OUT_CONN);
				if (clientConn != null) {
					shutdownClient(clientConn);
					LOG.debug("client conn closed - " + clientConn);
				}
			}
		} catch (Exception e) {
			handleException(e);
		} finally {
			shutdown(conn);
			countDown();
			DC.remove();
		}
	}

	void handleException(Exception e) {
		//Connection reset by peer: socket write error
		if (e instanceof SSLHandshakeException || e instanceof SocketException) {
			LOG.trace(e.getClass() + ": " + e.getMessage() + " - " + conn);
		} else if (e instanceof ConnectionClosedException) {
			LOG.trace("client closed connection. - " + conn);
		} else if (e instanceof SocketTimeoutException) {
			LOG.trace("timeout >> close connection. - " + conn);
		} else if (e instanceof RuntimeIOException) {
			//SocketException: Broken pipe
			LOG.warn(e.getClass() + ": " + e.getMessage() + " - " + conn);
			LOG.trace(ExceptionUtils.getStackTrace(e));
		} else {
			LOG.error(e.getClass() + ": " + e.getMessage() + " - " + conn);
			LOG.debug(ExceptionUtils.getStackTrace(e));
		}
	}

	boolean isClosed() {
		return socket.isClosed();
	}

	void shutdownClient(HttpConnection clientConn) {
		if (clientConn != null) {
			try {
				clientConn.close();
				if (LOG.isTraceEnabled()) LOG.trace("client conn closed. - " + clientConn);
				clientConn.shutdown();
				if (LOG.isTraceEnabled()) LOG.trace("client conn shutdown. - " + clientConn);
			} catch (IOException ignore) {
			}
		}
	}

	void shutdown(HttpConnection conn) {
		try {
			if (conn != null) {
				conn.shutdown();
				LOG.debug("server conn shutdown. - " + conn);
			}
		} catch (IOException ignore) {
		} finally {
			DC.remove();
		}
	}

	void countUp() {
		if (counter != null) counter.countUp();
	}

	void countDown() {
		if (counter != null) counter.countDown();
	}
}
