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

public class WebSocketHandler extends VelocityHttpHandler {
	
	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {

		if (WebSocketUtils.isWebSocket(request)) {
			Header[] headers = request.getAllHeaders();
			for (Header h : headers) {
				System.out.println("[request]"+h);
			}
			String protocol = WebSocketUtils.getWebSocketProtocol(request);
			System.out.println("protocol=" + protocol);
			WebSocket websocket = doWebSocketConnect(request, response, protocol);
			if (websocket != null) {
				System.out.println("websocket=" + websocket);

				WebSocketFactory factory = new WebSocketFactory();
				factory.upgrade(request, response, context, websocket, protocol);

				//ResponseUtils.setEntity(
				//	response, getEntity(WebSocketUtils.getFrameData("open")));
				//EntityUtils.consume(response.getEntity());

				Header[] resheaders = response.getAllHeaders();
				for (Header h : resheaders) {
					System.out.println("[response]"+h);
				}
			}
		} else {
			super.doRequest(request, response, context);
		}
	}
    
	protected WebSocket doWebSocketConnect(
			HttpRequest request, HttpResponse response, String protocol) {
		return new WebSocketImpl(request, response, protocol);
	}
}
