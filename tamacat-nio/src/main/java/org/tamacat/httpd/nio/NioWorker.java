/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.jmx.BasicCounter;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

public class NioWorker extends Thread {

	static final Log LOG = LogFactory.getLog(NioWorker.class);
    private HttpService service;
    private DefaultHttpServerConnection conn;
    private BasicCounter counter;
    
    /**
     * <p>Constructs with the specified {@link HttpService}.
     * @param service
     * @param insocket
     * @param params
     * @throws IOException
     */
    public NioWorker(
    		HttpService service, Socket insocket, 
    		HttpParams params, BasicCounter counter) throws IOException {
    	this.service = service;
    	this.conn = new DefaultHttpServerConnection();
    	this.conn.bind(insocket, params);
    	this.counter = counter;
    }

    @Override
	public void run() {
    	try {
        	counter.countUp();
        	LOG.trace("New connection thread");
            HttpContext context = new BasicHttpContext(null);
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
        } finally {
        	counter.countDown();
        }
    }
}
