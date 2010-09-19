/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.protocol.AsyncNHttpServiceHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class DefaultAsyncNHttpServiceHandler extends AsyncNHttpServiceHandler {
    static final Log LOG = LogFactory.getLog(DefaultAsyncNHttpServiceHandler.class);
    
	public DefaultAsyncNHttpServiceHandler(HttpProcessor httpProcessor,
			HttpResponseFactory responseFactory,
			ConnectionReuseStrategy connStrategy, HttpParams params) {
		super(httpProcessor, responseFactory, connStrategy, params);
	}
	
	public DefaultAsyncNHttpServiceHandler(HttpProcessor httpProcessor, HttpParams params) {
        super(httpProcessor,
        		new DefaultHttpResponseFactory(),
        		new DefaultConnectionReuseStrategy(),
        		params
        );
	}
    
	@Override
    public void requestReceived(final NHttpServerConnection conn) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("#requestReceived() >> " + conn.getHttpRequest().getRequestLine().getUri());
		}
		super.requestReceived(conn);
	}
	
    @Override
    protected void responseComplete(HttpResponse response, HttpContext context) {
    	LOG.trace("response complete");
        //System.out.println("#responseComplete()");
//        NHttpServerConnection conn
//        	= (NHttpServerConnection)context.getAttribute(ExecutionContext.HTTP_CONNECTION);
//        try {
//			if (conn != null) conn.shutdown();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }
}
