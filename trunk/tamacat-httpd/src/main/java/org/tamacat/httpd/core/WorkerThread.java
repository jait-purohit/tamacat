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
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WorkerThread extends Thread {
	static final Log LOG = LogFactory.getLog(WorkerThread.class);
	
	public static final String HTTP_IN_CONN = "http.proxy.in-conn";
    public static final String HTTP_OUT_CONN = "http.proxy.out-conn";
    static final String HTTP_CONN_KEEPALIVE = "http.proxy.conn-keepalive";
    
    private HttpService service;
    private DefaultHttpServerConnection inconn = new DefaultHttpServerConnection();
    private HttpClientConnection outconn; //optional.
    
    public WorkerThread(HttpService service, Socket insocket, HttpParams params) throws IOException {
    	this.service = service;
    	inconn.bind(insocket, params);
    }
    
    public void init() {
    	inconn = null;
    	outconn = null;
    }
    
    @Override
	public void run() {
    	LOG.trace("New connection thread");
        HttpContext context = new BasicHttpContext(null);
        
        // Bind connection objects to the execution context
        context.setAttribute(HTTP_IN_CONN, this.inconn);
        //context.setAttribute(HTTP_OUT_CONN, this.outconn);
        
        try {
            while (! Thread.interrupted()) {
                if (! this.inconn.isOpen()) {
                //	  this.outconn = (HttpClientConnection) context.getAttribute(HTTP_OUT_CONN);
                //    close(this.outconn);
                    break;
                }
                this.service.handleRequest(inconn, context);
                
                Boolean keepalive = (Boolean) context.getAttribute(HTTP_CONN_KEEPALIVE);
                if (! Boolean.TRUE.equals(keepalive)) {
                	//this.outconn = (HttpClientConnection) context.getAttribute(HTTP_OUT_CONN);
                	//close(outconn);
                    close(this.inconn);
                    break;
                }
            }
        } catch (ConnectionClosedException ex) {
        	//ex.printStackTrace();
        	LOG.error("Client closed connection");
        } catch (SocketTimeoutException ex) {
        	LOG.trace("timeout >> close connection.");
        } catch (Exception ex) {
        	LOG.error("Error: " + ex.getMessage());
        	//ex.printStackTrace();
        } finally {
            try {
                this.inconn.shutdown();
            } catch (IOException ignore) {}
            try {
            	this.outconn = (HttpClientConnection) context.getAttribute(HTTP_OUT_CONN);
            	if (this.outconn != null) this.outconn.shutdown();
            } catch (IOException ignore) {}
        }
    }
    
    void close(Object conn) throws ConnectionClosedException, SocketTimeoutException{
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
