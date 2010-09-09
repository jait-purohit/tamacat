/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;

public class WebSocketClient {

	public static final String XML = "components.xml";
	
	public static void main(String[] args) {
		String config = args.length > 0 ? args[0] : XML;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		HttpEngine server = di.getBean("server", HttpEngine.class);
		Thread t = new Thread(server);
		t.start();
		
		HttpClient client = new DefaultHttpClient();
		
		HttpGet request = new HttpGet("http://localhost/api/test");
		request.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		//request.getParams().setParameter(
		//		CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.TRUE);
		
		request.addHeader("Upgrade", "WebSocket");
		request.addHeader("Connection", "Upgrade");
		request.addHeader("Host", "localhost");
		request.addHeader("Origin", "http://localhost");
		request.addHeader("WebSocket-Protocol", "localhost");
		HttpResponse response = null;
		try {
			response = client.execute(request);
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        System.out.println(response.getLastHeader("Content-Encoding"));
	        System.out.println(response.getLastHeader("Content-Length"));
	        System.out.println("----------------------------------------");
			Header[] headers = response.getAllHeaders();
			for (Header h : headers) {
				System.out.println(h);
			}
			System.out.println(response.getStatusLine());
			String line = "ws://localhost/api/test";
			System.out.println(line);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
