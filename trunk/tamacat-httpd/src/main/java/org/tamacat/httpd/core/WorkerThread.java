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
import org.apache.http.HttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.jmx.PerformanceCounter;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.IOUtils;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class WorkerThread extends Thread {
	static final Log LOG = LogFactory.getLog(WorkerThread.class);
	
    static final String HTTP_OUT_CONN = "http.proxy.out-conn";
    static final String HTTP_CONN_KEEPALIVE = "http.proxy.conn-keepalive";
    static final String CONNECTION_DO_NOT_CLOSED = HttpServerConnection.class.getName() + ".__DO_NOT_CLOSED__";
    
	protected HttpService service;
	protected ServerHttpConnection conn;
	protected PerformanceCounter counter;
	protected Socket insocket;
    protected boolean isTrace;
    
    /**
     * <p>Constructs with the specified {@link HttpService}.
     * @param service
     * @param insocket
     * @param params
     * @throws IOException
     */
    public WorkerThread(
    		HttpService service, Socket insocket, 
    		HttpParams params, PerformanceCounter counter) throws IOException {
    	this.service = service;
    	this.insocket = insocket;
    	this.conn = new ServerHttpConnection();
    	this.conn.bind(insocket, params);
    	this.counter = counter;
		isTrace = LOG.isTraceEnabled();
    	LOG.trace("New worker thread");
    }
    
    @Override
	public void run() {
    	counter.countUp();
        HttpContext context = null;
    	try {
            while (Thread.interrupted() == false) {
            	context = new BasicHttpContext(null);
                this.service.handleRequest(conn, context);
                HttpConnection clientConn = (HttpConnection) context.getAttribute(HTTP_OUT_CONN);
                if (clientConn != null && this.conn.isOpen() == false) { //already closed.
                    IOUtils.close(clientConn);
                    if (isTrace) LOG.trace("client connection closed. - " + clientConn);
                    break;
                }
                
                Boolean keepalive = (Boolean) context.getAttribute(HTTP_CONN_KEEPALIVE);
                if (keepalive == null || Boolean.TRUE.equals(keepalive) == false) { //not keep-alive
                	if (clientConn != null) {
                		IOUtils.close(clientConn);
                		if (isTrace) LOG.trace("client connection closed. - " + clientConn);
                	}
                    IOUtils.close(conn);
                    if (isTrace) LOG.trace("server connection closed. - " + conn);
                    break;
                } else {
                	if (isTrace) LOG.trace("Keep-Alive: " + Boolean.TRUE.equals(keepalive));
                }
            }
    	} catch (SSLHandshakeException e) {
    		LOG.debug(e.getMessage());
        } catch (ConnectionClosedException e) {
        	LOG.debug("Client closed connection");
        } catch (SocketTimeoutException e) {
        	LOG.debug("timeout >> close connection.");
        } catch (SocketException e) {
        	//Connection reset by peer: socket write error
        	LOG.debug("SocketException: " + e.getMessage());
        	if (LOG.isTraceEnabled()) {
        		LOG.trace(ExceptionUtils.getStackTrace(e)); //debug
        	}
        } catch (Exception e) {
        	LOG.error("Error: " + e.getMessage());
        	LOG.debug(ExceptionUtils.getStackTrace(e)); //debug
        } finally {
            if (context != null && context.getAttribute(CONNECTION_DO_NOT_CLOSED) == null) {
            	shutdown();
            } else {
            	conn.setWebSocketSupport(true);
            }
        	counter.countDown();
        }
    }
        
    public boolean isClosed() {
    	return insocket.isClosed();
    }
    
    public void shutdown() {
        try {
            conn.shutdown();
            if (isTrace) LOG.trace("shutdown() - " + conn);
        } catch (IOException ignore) {
        }
    }
}
