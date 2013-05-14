package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicRequestLine;
import org.junit.Test;

public class WebSocketRequestFactoryTest {

	@Test
	public void testNewHttpRequestRequestLine() {
		WebSocketRequestFactory factory = new WebSocketRequestFactory();
		try {
			HttpRequest request = factory.newHttpRequest(
					new BasicRequestLine("GET", "/", new ProtocolVersion("HTTP",1,1)));
			assertEquals(true, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
	}

	@Test
	public void testNewHttpRequestStringString() {
		WebSocketRequestFactory factory = new WebSocketRequestFactory();
		try {
			HttpRequest request = factory.newHttpRequest("GET", "/");
			assertEquals(true, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
	}

}
