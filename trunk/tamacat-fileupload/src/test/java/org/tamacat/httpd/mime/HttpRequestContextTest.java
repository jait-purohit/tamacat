package org.tamacat.httpd.mime;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpRequestContextTest {
	HttpRequestContext ctx;
	HttpRequest request;
	
	@Before
	public void setUp() throws Exception {
		request = new BasicHttpRequest("GET", "/test.html");
		ctx = new HttpRequestContext(request);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetHttpRequest() {
		assertSame(request, ctx.getHttpRequest());
	}

	@Test
	public void testGetCharacterEncoding() {
		assertEquals(null, ctx.getCharacterEncoding());
		
		request.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
		assertEquals("UTF-8", ctx.getCharacterEncoding());
	}

	@Test
	public void testGetContentLength() {
		assertEquals(0, ctx.getContentLength());
	}

	@Test
	public void testGetContentType() {
		assertEquals(null, ctx.getContentType());
		
		request.addHeader(HTTP.CONTENT_TYPE, "text/plain");
		assertEquals("text/plain", ctx.getContentType());
	}

	@Test
	public void testGetInputStream() throws Exception {
		ctx.getInputStream();
	}

}
