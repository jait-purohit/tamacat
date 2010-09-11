/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.core.VelocityHttpHandler;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebSocketHandler extends VelocityHttpHandler {
	
	static final Log LOG = LogFactory.getLog(WebSocketHandler.class);
	
	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {

		if (WebSocketUtils.isWebSocket(request)) {
			if (LOG.isTraceEnabled()) {
				Header[] headers = request.getAllHeaders();
				for (Header h : headers) {
					LOG.trace("[Request]"+h);
				}
			}
			String protocol = WebSocketUtils.getWebSocketProtocol(request);
			WebSocket websocket = doWebSocketConnect(request, response, protocol);
			if (websocket != null) {
				LOG.trace("websocket=" + websocket);

				WebSocketFactory factory = new WebSocketFactory();
				factory.upgrade(request, response, context, websocket, protocol);

				if (LOG.isTraceEnabled()) {
					Header[] resheaders = response.getAllHeaders();
					for (Header h : resheaders) {
						LOG.trace("[Response]"+h);
					}
				}
			}
		} else {
			super.doRequest(request, response, context);
		}
	}
    
	protected WebSocket doWebSocketConnect(
			HttpRequest request, HttpResponse response, String protocol) {
		return new WebSocketImpl();
	}
}
