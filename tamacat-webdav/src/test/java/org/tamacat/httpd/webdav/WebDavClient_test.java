/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;

public class WebDavClient_test {

	public static final String XML = "httpd.xml";
	
	public static void main(String[] args) throws Exception {
		String config = args.length > 0 ? args[0] : XML;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		HttpEngine server = di.getBean("server", HttpEngine.class);
		Thread t = new Thread(server);
		t.start();
		
		create();
		
		delete();
		
		System.exit(0);
	}
	
	static void create() throws Exception {
		HttpClient client = new DefaultHttpClient();
		
		HttpPut request = new HttpPut("http://localhost:8080/webdav/test.txt");
		request.setEntity(new StringEntity("test1234","UTF-8"));
		request.addHeader("Host", "localhost");
		HttpResponse response = null;
		try {
			response = client.execute(request);
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        System.out.println("----------------------------------------");
			Header[] headers = response.getAllHeaders();
			for (Header h : headers) {
				System.out.println(h);
			}
			System.out.println(response.getStatusLine());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void delete() {
		HttpClient client = new DefaultHttpClient();
		
		HttpDelete request = new HttpDelete("http://localhost:8080/webdav/test.txt");
		request.addHeader("Host", "localhost");
		HttpResponse response = null;
		try {
			response = client.execute(request);
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        System.out.println("----------------------------------------");
			Header[] headers = response.getAllHeaders();
			for (Header h : headers) {
				System.out.println(h);
			}
			System.out.println(response.getStatusLine());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
