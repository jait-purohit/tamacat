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
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;
import org.tamacat.servlet.util.ServletUtils;
import org.tamacat.util.StringUtils;

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
		
		//"test1=value1; expires=1260794052265; domain=www.example.com; path=/"
		String cookieLine = res.getFirstHeader("Set-Cookie").getValue();
		assertEquals("value1", HeaderUtils.getCookieValue(cookieLine, "test1"));
		assertEquals("www.example.com", HeaderUtils.getCookieValue(cookieLine, "domain"));
		assertEquals("/", HeaderUtils.getCookieValue(cookieLine, "path"));
	}

	@Test
	public void testAddDateHeader() {
		String value = "Tue, 11 Jun 2002 15:33:25 GMT";
		long time = ServletUtils.getTime(value);
		
		response.addDateHeader("If-Modified-Since", time);
		assertEquals(value,	res.getFirstHeader("If-Modified-Since").getValue());
	}

	@Test
	public void testAddHeader() {
		String value = "Value1";
		
		response.addHeader("Test", value);
		assertEquals(value,	res.getFirstHeader("Test").getValue());
	}

	@Test
	public void testAddIntHeader() {
		int value = 123;
		
		response.addIntHeader("Test", value);
		assertEquals(value,	StringUtils.parse(res.getFirstHeader("Test").getValue(),-1));
	}

	@Test
	public void testContainsHeader() {
		String value = "Value1";
		response.addHeader("Test", value);

		assertEquals(true, response.containsHeader("Test"));
		assertEquals(false, response.containsHeader("Test2"));
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
		String value = "Tue, 11 Jun 2002 15:33:25 GMT";
		long time = ServletUtils.getTime(value);
		
		response.setDateHeader("If-Modified-Since", time);
		assertEquals(value,	res.getFirstHeader("If-Modified-Since").getValue());
	}

	@Test
	public void testSetHeader() {
		String value = "Value1";
		
		response.setHeader("Test", value);
		assertEquals(value,	res.getFirstHeader("Test").getValue());
	}

	@Test
	public void testSetIntHeader() {
		int value = 123;
		
		response.setIntHeader("Test", value);
		assertEquals(value,	StringUtils.parse(res.getFirstHeader("Test").getValue(),-1));
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
