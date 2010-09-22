package org.tamacat.httpd.websocket;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.BufferingNHttpEntity;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.nio.AbstractNHttpHandler;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebSocketNHttpHandler extends AbstractNHttpHandler {

	static final Log LOG = LogFactory.getLog(WebSocketNHttpHandler.class);

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
				context.setAttribute(
					WebSocketNHttpHandler.class.getName()+".WebSocket", websocket);
				
				handleResponse(request, response, context);
			}
		}
	}
	

	protected void handleResponse(
			HttpRequest request, HttpResponse response, 
			HttpContext context) {
		System.out.println("entityRequest()");
		String protocol = WebSocketUtils.getWebSocketProtocol(request);
		WebSocket websocket = (WebSocket) context.getAttribute(
				WebSocketNHttpHandler.class.getName()+".WebSocket");
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
	}
	
	@Override
	public ConsumingNHttpEntity entityRequest(HttpEntityEnclosingRequest request,
	            HttpContext context)
	    throws HttpException, IOException {
        // Buffer imcoming content in memory for simplicity 
        return new BufferingNHttpEntity(request.getEntity(),
                new HeapByteBufferAllocator());
    }
	
	protected WebSocket doWebSocketConnect(
			HttpRequest request, HttpResponse response, String protocol) {
		return new WebSocketImpl();
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			NStringEntity entity = new NStringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}

	@Override
	protected HttpEntity getFileEntity(File file) {
		NFileEntity body = new NFileEntity(file, getContentType(file));
        return body;
	}
}
