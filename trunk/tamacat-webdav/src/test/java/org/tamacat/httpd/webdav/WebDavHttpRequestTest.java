package org.tamacat.httpd.webdav;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bradmcevoy.http.Request;

public class WebDavHttpRequestTest {

	HttpRequest req;
	WebDavHttpRequest request;
	
	@Before
	public void setUp() throws Exception {
		req = new BasicHttpRequest("GET", "http://www.example.com/test/");
		request = new WebDavHttpRequest(req, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWebDavRequest() {
	}

	@Test
	public void testGetRequestHeaderHeader() {
	}

	@Test
	public void testGetAbsoluteUrl() {
		assertEquals("http://www.example.com/test/", request.getAbsoluteUrl());
	}

	@Test
	public void testGetAuthorization() {
	}

	@Test
	public void testGetFromAddress() {
	}

	@Test
	public void testGetHeaders() {
	}

	@Test
	public void testGetInputStream() {

	}

	@Test
	public void testGetMethod() {
		assertEquals(Request.Method.GET, new WebDavHttpRequest(new BasicHttpRequest("GET", "/test/"),null).getMethod());
		assertEquals(Request.Method.POST, new WebDavHttpRequest(new BasicHttpRequest("POST", "/test/"),null).getMethod());
		assertEquals(Request.Method.PUT, new WebDavHttpRequest(new BasicHttpRequest("PUT", "/test/"),null).getMethod());
		assertEquals(Request.Method.DELETE, new WebDavHttpRequest(new BasicHttpRequest("DELETE", "/test/"),null).getMethod());
		assertEquals(Request.Method.HEAD, new WebDavHttpRequest(new BasicHttpRequest("HEAD", "/test/"),null).getMethod());
		assertEquals(Request.Method.COPY, new WebDavHttpRequest(new BasicHttpRequest("COPY", "/test/"),null).getMethod());
		assertEquals(Request.Method.LOCK, new WebDavHttpRequest(new BasicHttpRequest("LOCK", "/test/"),null).getMethod());
		assertEquals(Request.Method.MKCOL, new WebDavHttpRequest(new BasicHttpRequest("MKCOL", "/test/"),null).getMethod());
		assertEquals(Request.Method.MOVE, new WebDavHttpRequest(new BasicHttpRequest("MOVE", "/test/"),null).getMethod());
		assertEquals(Request.Method.OPTIONS, new WebDavHttpRequest(new BasicHttpRequest("OPTIONS", "/test/"),null).getMethod());
		assertEquals(Request.Method.PROPFIND, new WebDavHttpRequest(new BasicHttpRequest("PROPFIND", "/test/"),null).getMethod());
		assertEquals(Request.Method.PROPPATCH, new WebDavHttpRequest(new BasicHttpRequest("PROPPATCH", "/test/"),null).getMethod());
		assertEquals(Request.Method.TRACE, new WebDavHttpRequest(new BasicHttpRequest("TRACE", "/test/"),null).getMethod());
		assertEquals(Request.Method.UNLOCK, new WebDavHttpRequest(new BasicHttpRequest("UNLOCK", "/test/"),null).getMethod());
	}

	@Test
	public void testParseRequestParameters() {
	}

}
