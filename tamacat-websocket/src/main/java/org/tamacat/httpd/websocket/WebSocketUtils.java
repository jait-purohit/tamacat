/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.util.StringUtils;

public class WebSocketUtils {

	static final char beginFrame00 = 0x00;
	static final char beginFrame80 = 0x80;
	static final char endFrame = 0xFF;
	
	public static boolean isWebSocket(HttpRequest request) {
		boolean isHTTP_1_1 = HttpVersion.HTTP_1_1.equals(request.getProtocolVersion());
		String upg = HeaderUtils.getHeader(request, "Upgrade");
		if (isHTTP_1_1 && "WebSocket".equals(upg)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isSecureWebSocket(HttpRequest request) {
		return request.getFirstHeader("Sec-WebSocket-Key1") != null;
	}
	
	public static String getWebSocketProtocol(HttpRequest request) {
		String protocol = null;
		if (isSecureWebSocket(request)) {
			protocol = HeaderUtils.getHeader(request, "Sec-WebSocket-Protocol");
		}
		if (StringUtils.isEmpty(protocol)) {
			protocol = HeaderUtils.getHeader(request, "WebSocket-Protocol");
		}
		return protocol;
	}
	
//	public static void setHandshakeStatus(HttpResponse response) {
//		response.setStatusLine(HttpVersion.HTTP_1_1, 101);
//		response.setReasonPhrase("WebSocket Protocol Handshake");
//	}
	
	public static void setResponseUpgradeHeader(
			HttpResponse response, String origin, String url, String protocol) {
		response.addHeader("Upgrade", "WebSocket");
		response.addHeader("Connection", "Upgrade");
		response.addHeader("WebSocket-Origin", origin);
		response.addHeader("WebSocket-Location", url);
		if (protocol != null) {
			response.addHeader("WebSocket-Protocol", protocol);
		}
		response.setStatusLine(HttpVersion.HTTP_1_1, 101);
		response.setReasonPhrase("Web Socket Protocol Handshake");
	}
	
	public static void setSecureResponseUpgradeHeader(
			HttpResponse response, String origin, String url, String protocol) {
		response.addHeader("Upgrade", "WebSocket");
		response.addHeader("Connection", "Upgrade");
		response.addHeader("Sec-WebSocket-Origin", origin);
		response.addHeader("Sec-WebSocket-Location", url);
		if (protocol != null) {
			response.addHeader("Sec-WebSocket-Protocol", protocol);
		}
		response.setStatusLine(HttpVersion.HTTP_1_1, 101);
		response.setReasonPhrase("WebSocket Protocol Handshake");
	}
	
	public static String getFrameData(String data) {
		return beginFrame00 + data + endFrame;
	}
	
    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	public static HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
}
