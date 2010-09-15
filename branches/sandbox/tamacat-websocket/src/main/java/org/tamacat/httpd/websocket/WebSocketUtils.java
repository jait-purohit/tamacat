/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	
	//use Draft-76?
	public static boolean isSecureWebSocket(HttpRequest request) {
		return request.getFirstHeader("Sec-WebSocket-Key1") != null;
	}
	
	//Subprotocol names are sequences of one or more characters in the
	//range U+0021 to U+007F
	public static String getWebSocketProtocol(HttpRequest request) {
		String protocol = null;
		//for Draft-76
		if (isSecureWebSocket(request)) {
			protocol = HeaderUtils.getHeader(request, "Sec-WebSocket-Protocol");
		}
		if (StringUtils.isEmpty(protocol)) {
			protocol = HeaderUtils.getHeader(request, "WebSocket-Protocol");
		}
		return protocol;
	}
	
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
	
	//for Draft-76
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
	
    static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	public static HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	public static byte[] getSecureWebSocketMD5Response(
			String key1, String key2, byte[] key3) {
		long k1 = hixieCrypt(key1);
		long k2 = hixieCrypt(key2);
        //System.out.println(Long.toHexString(k1));
        //System.out.println(Long.toHexString(k2));
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] fodder = new byte[16];

			fodder[0] = (byte) (0xff & (k1 >> 24));
			fodder[1] = (byte) (0xff & (k1 >> 16));
			fodder[2] = (byte) (0xff & (k1 >> 8));
			fodder[3] = (byte) (0xff & k1);
			fodder[4] = (byte) (0xff & (k2 >> 24));
			fodder[5] = (byte) (0xff & (k2 >> 16));
			fodder[6] = (byte) (0xff & (k2 >> 8));
			fodder[7] = (byte) (0xff & k2);
			for (int i = 0; i < 8; i++) {
				fodder[8 + i] = key3[i];
			}
			//System.out.println(new String(fodder));
			md.update(fodder);
			byte[] result = md.digest();
			return result;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	static long hixieCrypt(String key) {
		long number = 0;
		int spaces = 0;
		for (char c : key.toCharArray()) {
			if (Character.isDigit(c)) {
				number = (number * 10) + (c - '0');
			} else if (c == ' '){
				spaces++;
			}
		}
		return spaces > 0 ? (number / spaces) : 0;
	}
}
