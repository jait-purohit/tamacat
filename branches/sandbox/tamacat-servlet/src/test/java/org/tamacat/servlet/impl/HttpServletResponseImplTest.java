package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import javax.servlet.http.Cookie;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;

public class HttpServletResponseImplTest {

	ServiceUrl serviceUrl;
	HttpRequest req;
	HttpResponse res;
	HttpContext context;
	HttpCoreServletRequest request;
	HttpCoreServletResponse response;
	
	@Before
	public void setUp() throws Exception {
		req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user");
		res = new BasicHttpResponse(new BasicStatusLine(
				new ProtocolVersion("HTTP",1,1), 200, "OK"));
		context = new BasicHttpContext();

		ServiceConfigXmlParser parser = new ServiceConfigXmlParser(new ServerConfig());
		serviceUrl = parser.getServiceConfig().getServiceUrl("/test/");
		request = new HttpServletObjectFactory(serviceUrl).createRequest(req, context);
		response = new HttpServletObjectFactory(serviceUrl).createResponse(res, context);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddCookie() {
		Cookie cookie = new Cookie("test1", "value1");
		cookie.setDomain("www.example.com");
		cookie.setPath("/");
		cookie.setMaxAge(30);
		cookie.setSecure(false);
		response.addCookie(cookie);
		
		System.out.println(res.getFirstHeader("Set-Cookie"));
	}

	@Test
	public void testAddDateHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddIntHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testContainsHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncodeRedirectURL() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncodeRedirectUrl() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncodeURL() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncodeUrl() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendErrorInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendErrorIntString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendRedirect() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDateHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetIntHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetStatusInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetStatusIntString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFlushBuffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBufferSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCharacterEncoding() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetContentType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLocale() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOutputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWriter() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsCommitted() {
		fail("Not yet implemented");
	}

	@Test
	public void testReset() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetBuffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBufferSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCharacterEncoding() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetContentLength() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetContentType() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetLocale() {
		fail("Not yet implemented");
	}

}
