/*
 * Copyright (c) 2009, TamaCat.org
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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.jmx.PerformanceCounter;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class WorkerThread extends Thread {
	static final Log LOG = LogFactory.getLog(WorkerThread.class);
	static final String HTTP_IN_CONN = "http.in-conn";
	static final String HTTP_OUT_CONN = "http.out-conn";

	ServerConfig config;

	protected HttpService service;
	protected ServerHttpConnection conn;
	protected PerformanceCounter counter;
	protected Socket insocket;

	public WorkerThread(//String threadName,
			HttpService service, Socket insocket,
			ServerConfig config, PerformanceCounter counter) throws IOException {
		this.service = service;
		this.insocket = insocket;
		this.conn = new ServerHttpConnection(config.getSocketBufferSize());
		this.conn.bind(insocket);
		this.counter = counter;
		//setName(getName().replace("Thread", threadName));
	}

	@Override
	public void run() {
		LOG.debug("start worker thread - " + conn);
		counter.countUp();
		HttpContext parent = new BasicHttpContext(); //for Keep-Alive timeout
		// Bind connection objects to the execution context
		parent.setAttribute(HTTP_IN_CONN, conn);
		try {
			HttpConnectionMetrics metrics = this.conn.getMetrics();
			while (Thread.interrupted() == false && conn.isOpen()) {
				LOG.debug("count:" + metrics.getRequestCount() + " - " + conn);
				HttpContext context = new BasicHttpContext(parent);
				this.service.handleRequest(conn, context);

				// close client connection. (unsupported keep-alive)
				HttpConnection clientConn = (HttpConnection) context.getAttribute(HTTP_OUT_CONN);
				if (clientConn != null) {
					shutdownClient(clientConn);
					LOG.debug("client connection closed - " + clientConn);
				}
			}
		} catch (Exception e) {
			handleException(e);
		} finally {
			shutdown(conn);
			counter.countDown();
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

	public boolean isClosed() {
		return insocket.isClosed();
	}

	void shutdownClient(HttpConnection clientConn) {
		try {
			clientConn.close();
			if (LOG.isTraceEnabled()) LOG.trace("client connection closed. - " + clientConn);
			clientConn.shutdown();
			if (LOG.isTraceEnabled()) LOG.trace("client connection shutdown. - " + clientConn);
		} catch (IOException ignore) {
		}
	}

	public void shutdown(HttpConnection conn) {
		try {
			conn.shutdown();
			LOG.debug("server connection shutdown. - " + conn);
		} catch (IOException ignore) {
		}
	}
}
