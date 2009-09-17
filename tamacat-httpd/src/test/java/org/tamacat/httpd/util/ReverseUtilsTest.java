/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;

public class ReverseUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRemoveRequestHeaders() {
		HttpRequest request = new BasicHttpRequest("GET", "/");
		
		request.setHeader("Transfer-Encoding", "gzip");
		request.setHeader("Content-Length", "123456");
		request.setHeader("Content-Type", "text/html");

		ReverseUtils.removeRequestHeaders(request);
		
		assertNull(request.getFirstHeader("Transfer-Encoding"));
		assertNull(request.getFirstHeader("Content-Length"));
		assertEquals("text/html", request.getFirstHeader("Content-Type").getValue());
	}

	@Test
	public void testCopyHttpResponse() {
		HttpResponse targetResponse = new BasicHttpResponse(
			new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		
		targetResponse.setHeader("Transfer-Encoding", "gzip");
		targetResponse.setHeader("Content-Length", "123456");
		targetResponse.setHeader("Content-Type", "text/html");
		targetResponse.setHeader("Host", "tamacat.org");
		HttpResponse response = new BasicHttpResponse(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		
		ReverseUtils.copyHttpResponse(targetResponse, response);
		
		assertNull(response.getFirstHeader("Transfer-Encoding"));
		assertNull(response.getFirstHeader("Content-Length"));
		assertNull(response.getFirstHeader("Content-Type"));
		assertEquals("tamacat.org", response.getFirstHeader("Host").getValue());
	}

}
