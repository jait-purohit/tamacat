/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.jmx.PerformanceCounter;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class WorkerThread extends Thread {
	static final Log LOG = LogFactory.getLog(WorkerThread.class);
	
    private HttpService service;
    private ServerHttpConnection conn;
    private PerformanceCounter counter;
    private Socket insocket;
    
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
    }
    
    @Override
	public void run() {
    	counter.countUp();
        HttpContext context = new BasicHttpContext(null);
    	try {
        	LOG.trace("New connection thread");
            this.service.handleRequest(conn, context);
        } catch (ConnectionClosedException ex) {
        	LOG.debug("Client closed connection");
        } catch (SocketTimeoutException ex) {
        	LOG.debug("timeout >> close connection.");
        } catch (Exception ex) {
        	//ex.printStackTrace();
        	LOG.error("Error: " + ex.getMessage());
        	LOG.trace(ExceptionUtils.getStackTrace(ex)); //debug
        } finally {
            if (context.getAttribute(
            		HttpServerConnection.class.getName()
            		+ ".__DO_NOT_CLOSED__") == null) {
            	shutdown();
            } else {
            	conn.setWebSocket(true);
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
            conn.close();
        } catch (IOException ignore) {
        }
    }
}
