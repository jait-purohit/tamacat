/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class WorkerThread extends Thread {
	static final Log LOG = LogFactory.getLog(WorkerThread.class);
	
    private HttpService service;
    private DefaultHttpServerConnection conn;
    
    /**
     * <p>Constructs with the specified {@link HttpService}.
     * @param service
     * @param insocket
     * @param params
     * @throws IOException
     */
    public WorkerThread(HttpService service, Socket insocket, HttpParams params) throws IOException {
    	this.service = service;
    	this.conn = new DefaultHttpServerConnection();
    	this.conn.bind(insocket, params);
    }
    
    @Override
	public void run() {
    	LOG.debug("New connection thread");
        HttpContext context = new BasicHttpContext(null);
        try {
            this.service.handleRequest(conn, context);
        } catch (ConnectionClosedException ex) {
        	LOG.debug("Client closed connection");
        } catch (SocketTimeoutException ex) {
        	LOG.debug("timeout >> close connection.");
        } catch (Exception ex) {
        	LOG.error("Error: " + ex.getMessage());
        	LOG.trace(ExceptionUtils.getStackTrace(ex)); //debug
        } finally {
        	shutdown();
        }
    }
    
    private void shutdown() {
        try {
            conn.shutdown();
            conn.close();
        } catch (IOException ignore) {
        }
    }
}
