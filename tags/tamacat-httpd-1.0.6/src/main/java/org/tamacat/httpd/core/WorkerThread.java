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
    static final String HTTP_IN_CONN = "http.proxy.in-conn";
    static final String HTTP_OUT_CONN = "http.proxy.out-conn";
    static final String HTTP_CONN_KEEPALIVE = "http.proxy.conn-keepalive";
    static final String HTTP_KEEPALIVE_TIMEOUT = "http.proxy.keepalive-timeout";
    static final String HTTP_KEEPALIVE_START = "http.proxy.keepalive-start";
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
    	HttpContext parent = new BasicHttpContext(); //for Keep-Alive timeout
    	try {
    		while (Thread.interrupted() == false) {
                if (conn.isOpen() == false) { //already closed.
                    IOUtils.close(conn);
                    LOG.debug("server connection closed(isOpen:false). - " + conn);
                    break;
                }
            	HttpContext context = new BasicHttpContext(parent);
                // Bind connection objects to the execution context
                context.setAttribute(HTTP_IN_CONN, conn);
                this.service.handleRequest(conn, context);

                HttpConnection clientConn = (HttpConnection) context.getAttribute(HTTP_OUT_CONN);
                if (clientConn != null) {
                	IOUtils.close(clientConn);
                	shutdown(clientConn);
                	if (isTrace) LOG.trace("client connection closed. - " + clientConn);
                }
                
                //if (context.getAttribute(CONNECTION_DO_NOT_CLOSED) != null) {
                //	conn.setWebSocketSupport(true);
                //}
                
                // check Keep-Alive timeout.
                Boolean keepalive = (Boolean) context.getAttribute(HTTP_CONN_KEEPALIVE);
                Integer keepAliveTimeout = (Integer) context.getAttribute(HTTP_KEEPALIVE_TIMEOUT);
                Long keepAliveStart = (Long) parent.getAttribute(HTTP_KEEPALIVE_START);
                if (Boolean.TRUE.equals(keepalive) && keepAliveTimeout != null && keepAliveStart != null) {
                	long end = System.currentTimeMillis() - keepAliveStart;
                	if (end > keepAliveTimeout) {
                		keepalive = false; //timeout
                		LOG.debug("Keep-Alive Timeout: " + end + " > " + keepAliveTimeout + " msec.");
                	}
                }
                
                //if (keepalive != null) LOG.debug("Keep-Alive: " + keepalive);
                if (Boolean.TRUE.equals(keepalive) == false) { //not keep-alive -> close
	                IOUtils.close(conn);
	                LOG.debug("server connection closed. - " + conn);
	                break;
                }
                parent.setAttribute(HTTP_KEEPALIVE_START, new Long(System.currentTimeMillis()));
                LOG.debug("Keep-Alive: true -> waiting. - " + conn);
            }
    	} catch (SSLHandshakeException e) {
    		LOG.trace(e.getMessage());
        } catch (ConnectionClosedException e) {
        	LOG.trace("Client closed connection");
        } catch (SocketTimeoutException e) {
        	LOG.trace("timeout >> close connection.");
        } catch (SocketException e) {
        	//Connection reset by peer: socket write error
        	LOG.trace("SocketException: " + e.getMessage());
        	LOG.trace(ExceptionUtils.getStackTrace(e)); //debug
        } catch (Exception e) {
        	LOG.error("Error: " + e.getMessage());
        	LOG.debug(ExceptionUtils.getStackTrace(e)); //debug
        } finally {
           	shutdown(conn);
        	counter.countDown();
        }
    }
        
    public boolean isClosed() {
    	return insocket.isClosed();
    }
    
    public void shutdown() {
        try {
            conn.shutdown();
            LOG.debug("shutdown() - " + conn);
        } catch (IOException ignore) {
        }
    }
    
    public void shutdown(HttpConnection conn) {
        try {
            conn.shutdown();
            LOG.debug("shutdown() - " + conn);
        } catch (IOException ignore) {
        }
    }
}
