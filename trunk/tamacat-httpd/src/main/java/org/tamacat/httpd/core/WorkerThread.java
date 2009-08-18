/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpServerConnection;
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
    private DefaultHttpServerConnection inconn = new DefaultHttpServerConnection();
    
    public WorkerThread(HttpService service, Socket insocket, HttpParams params) throws IOException {
    	this.service = service;
    	inconn.bind(insocket, params);
    }
    
    public void init() {
    	inconn = null;
    }
    
    @Override
	public void run() {
    	LOG.trace("New connection thread");
        HttpContext context = new BasicHttpContext(null);
        try {
        	AccessLogUtils.setRemoteAddress(context, inconn.getRemoteAddress());
            this.service.handleRequest(inconn, context);
        } catch (ConnectionClosedException ex) {
        	LOG.error("Client closed connection");
        } catch (SocketTimeoutException ex) {
        	LOG.trace("timeout >> close connection.");
        } catch (Exception ex) {
        	LOG.error("Error: " + ex.getMessage());
        	//ex.printStackTrace();
        } finally {
            try {
                this.inconn.shutdown();
                close(inconn);
            } catch (IOException ignore) {}
        }
    }
    
    protected void close(Object conn)
    		throws ConnectionClosedException, SocketTimeoutException{
    	if (conn != null) {
    		try {
	    		if (conn instanceof Closeable) {
	    			((Closeable)conn).close();
	    		} else if (conn instanceof HttpClientConnection) {
	    			((HttpClientConnection)conn).close();
	    		} else if (conn instanceof HttpServerConnection) {
	    			((HttpServerConnection)conn).close();
	    		}
            } catch (ConnectionClosedException e) {
            	throw e;
            } catch (SocketTimeoutException e) {
            	throw e;
    		} catch (IOException e) {
    			LOG.error("I/O error: " + e.getMessage());
    		}
    	}
    }
}
