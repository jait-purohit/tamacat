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
import org.tamacat.httpd.util.AccessLogUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class WorkerThread extends Thread {
	static final Log LOG = LogFactory.getLog(WorkerThread.class);
	
    private HttpService service;
    private DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
    
    /**
     * <p>Constructs with the specified {@link HttpService}.
     * @param service
     * @param insocket
     * @param params
     * @throws IOException
     */
    public WorkerThread(HttpService service, Socket insocket, HttpParams params) throws IOException {
    	this.service = service;
    	conn.bind(insocket, params);
    }
    
    @Override
	public void run() {
    	LOG.trace("New connection thread");
        HttpContext context = new BasicHttpContext(null);
        try {
        	AccessLogUtils.setRemoteAddress(context, conn.getRemoteAddress());
            this.service.handleRequest(conn, context);
        } catch (ConnectionClosedException ex) {
        	LOG.error("Client closed connection");
        } catch (SocketTimeoutException ex) {
        	LOG.trace("timeout >> close connection.");
        } catch (Exception ex) {
        	LOG.error("Error: " + ex.getMessage());
        	//ex.printStackTrace();
        } finally {
            try {
                this.conn.shutdown();
                conn.close();
            } catch (IOException ignore) {}
        }
    }
}