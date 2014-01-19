package org.tamacat.httpd.webdav;

import static org.junit.Assert.*;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicRequestLine;
import org.junit.Test;

public class WebDavHttpRequestFactoryTest {

	@Test
	public void testIsOneOf() {
		String[] methods = new String[] {
			"HEAD", "GET", "POST"
		};
		assertTrue(WebDavHttpRequestFactory.isOneOf(methods, "GET"));
		assertTrue(WebDavHttpRequestFactory.isOneOf(methods, "POST"));
		assertTrue(WebDavHttpRequestFactory.isOneOf(methods, "HEAD"));
		
		assertFalse(WebDavHttpRequestFactory.isOneOf(methods, "PUT"));
		assertFalse(WebDavHttpRequestFactory.isOneOf(methods, "DELETE"));
	}

	@Test
	public void testNewHttpRequestRequestLine() {
		WebDavHttpRequestFactory factory = new WebDavHttpRequestFactory();
		try {
			HttpRequest request = factory.newHttpRequest(
					new BasicRequestLine("GET", "/", new ProtocolVersion("HTTP",1,1)));
			assertEquals(false, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			HttpRequest request = factory.newHttpRequest(
					new BasicRequestLine("HEAD", "/", new ProtocolVersion("HTTP",1,1)));
			assertEquals(false, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			HttpRequest request = factory.newHttpRequest(
					new BasicRequestLine("POST", "/", new ProtocolVersion("HTTP",1,1)));
			assertEquals(true, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			HttpRequest request = factory.newHttpRequest(
					new BasicRequestLine("MKCOL", "/", new ProtocolVersion("HTTP",1,1)));
			assertEquals(true, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			factory.newHttpRequest(new BasicRequestLine("ERROR", "/", new ProtocolVersion("HTTP",1,1)));
			fail();
		} catch (MethodNotSupportedException e) {
			assertTrue(true);
		};
		try {
			factory.newHttpRequest(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		} catch (MethodNotSupportedException e) {
			fail();
		};
	}

	@Test
	public void testNewHttpRequestStringString() {
		WebDavHttpRequestFactory factory = new WebDavHttpRequestFactory();
		try {
			HttpRequest request = factory.newHttpRequest("GET", "/");
			assertEquals(false, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			HttpRequest request = factory.newHttpRequest("HEAD", "/");
			assertEquals(false, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			HttpRequest request = factory.newHttpRequest("POST", "/");
			assertEquals(true, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			HttpRequest request = factory.newHttpRequest("MKCOL", "/");
			assertEquals(true, request instanceof HttpEntityEnclosingRequest);
		} catch (MethodNotSupportedException e) {
			fail();
		};
		
		try {
			factory.newHttpRequest("ERROR", "/");
			fail();
		} catch (MethodNotSupportedException e) {
			assertTrue(true);
		};
		
		try {
			factory.newHttpRequest(null, null);
			fail();
		} catch (MethodNotSupportedException e) {
			assertTrue(true);
		};
	}

}
