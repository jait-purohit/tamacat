package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.test.TestServlet;

public class HttpServletRequestImplTest {

	ServiceUrl serviceUrl;
	HttpRequest req;
	HttpContext context;
	HttpServletRequestImpl request;
	
	@Before
	public void setUp() throws Exception {
		req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user");
		context = new BasicHttpContext();

		ServiceConfigParser parser = new ServiceConfigParser(new ServerConfig());
		serviceUrl = parser.getConfig().getDefaultServiceConfig().getServiceUrl("/test/");
		
		ServletContextImpl servletContext = new ServletContextImpl(
				System.getProperty("user.dir")
				+ "/src/test/resources/test", serviceUrl);
		
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setServlet(new TestServlet());
		servletUrl.setUrlPattern("/index.html");
		servletUrl.setServletName("TestServlet");
		request = (HttpServletRequestImpl)
			new HttpServletObjectFactory(servletContext)
					.createRequest(servletUrl, req, context);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAuthType() {
		assertEquals(null, request.getAuthType());
	}

	@Test
	public void testGetContextPath() {
		assertEquals("/test", request.getContextPath());
	}

	@Test
	public void testGetCookies() {
		req.addHeader("Cookie", "user=admin; type=user");
		Cookie[] cookies = request.getCookies();
		assertEquals(2, cookies.length);
		assertEquals("user", cookies[0].getName());
		assertEquals("admin", cookies[0].getValue());
		assertEquals("type", cookies[1].getName());
		assertEquals("user", cookies[1].getValue());
	}

	@Test
	public void testGetDateHeader() {
		String value = "Tue, 11 Jun 2002 15:33:25 GMT";
		req.addHeader("If-Modified-Since", value);
		SimpleDateFormat df
			= new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		long time = -1;
		try {
			Date date = df.parse(value);
			time = date.getTime();
			assertEquals(true, time > 0);
		} catch (ParseException e) {
			fail();
		}
		assertEquals(time, request.getDateHeader("If-Modified-Since"));
	}

	@Test
	public void testGetHeader() {
		req.addHeader("Host", "www.example.com");
		assertEquals("www.example.com", request.getHeader("Host"));
	}

	@Test
	public void testGetHeaderNames() {
		req.addHeader("Host", "www.example.com");
		req.addHeader("Test", "123");
		Enumeration<?> names = request.getHeaderNames();
		assertEquals("Host", names.nextElement());
		assertEquals("Test", names.nextElement());
	}

	@Test
	public void testGetHeaders() {
		req.addHeader("Host", "www.example.com");
		req.addHeader("Host", "www2.example.com");
		Enumeration<?> values = request.getHeaders("Host");
		assertEquals("www.example.com", values.nextElement());
		assertEquals("www2.example.com", values.nextElement());
	}

	@Test
	public void testGetIntHeader() {
		req.addHeader("Test", "123");
		req.addHeader("TestNG", "abc");
		
		assertEquals(123, request.getIntHeader("Test"));
		assertEquals(-1, request.getIntHeader("TestNone"));
		try {
			request.getIntHeader("TestNG");
			fail();
		} catch (NumberFormatException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetMethod() {
		assertEquals("GET", request.getMethod());
	}

	@Test
	public void testGetPathInfo() {
		assertEquals(null, request.getPathInfo());
		//TODO add test case.
	}

	@Test
	public void testGetPathTranslated() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetQueryString() {
		assertEquals("/test/index.html?id=amdin&type=user", request.getQueryString());
	}

	@Test
	public void testGetRemoteUser() {
		assertEquals(null, request.getRemoteUser());

		context.setAttribute(AuthComponent.REMOTE_USER_KEY, "admin");
		assertEquals("admin", request.getRemoteUser());
	}

	@Test
	public void testGetRequestURI() {
		assertEquals("/test/index.html", request.getRequestURI());
	}

	@Test
	public void testGetRequestURL() {
		req.addHeader("Host", "www.example.com");
		
		StringBuffer url = request.getRequestURL();
		assertEquals("http://www.example.com/test/index.html", url.toString());
	}

	@Test
	public void testGetRequestedSessionId() {
		HttpSession session = request.getSession(true);
		assertEquals(session.getId(), request.getRequestedSessionId());
	}

	@Test
	public void testGetServletPath() {
		assertEquals("/index.html", request.getServletPath());
	}

	@Test
	public void testGetSession() {
		HttpSession session = request.getSession();
		assertNotNull(session);
	}

	@Test
	public void testGetSessionBoolean() {
		HttpSession session = request.getSession(false);
		assertNull(session);
		
		HttpSession session2 = request.getSession(true);
		assertNotNull(session2);
	}

	@Test
	public void testGetUserPrincipal() {
		context.setAttribute(AuthComponent.REMOTE_USER_KEY, "admin");

		Principal principal = request.getUserPrincipal();
		assertNotNull(principal);
		assertEquals("admin", principal.getName());
	}

	@Test
	public void testIsRequestedSessionIdFromCookie() {
		HttpSession session = request.getSession(true);
		req.setHeader("Cookie", "JSESSIONID=" + session.getId());
				
		assertTrue(request.isRequestedSessionIdFromCookie());
	}

	@Test
	public void testIsRequestedSessionIdFromURL() {
		HttpSession session = request.getSession(true);
		req.setHeader("Cookie", "JSESSIONID=" + session.getId());
		
		assertFalse(request.isRequestedSessionIdFromURL());
	}

	@Test
	public void testIsRequestedSessionIdFromUrl() {
		HttpSession session = request.getSession(true);
		req.setHeader("Cookie", "JSESSIONID=" + session.getId());
		
		assertFalse(request.isRequestedSessionIdFromUrl());
	}

	@Test
	public void testIsRequestedSessionIdValid() {
		assertFalse(request.isRequestedSessionIdValid());

		HttpSession session = request.getSession(true);
		assertNotNull(session);
		assertTrue(request.isRequestedSessionIdValid());
	}

	@Test
	public void testIsUserInRole() {
		assertEquals(false, request.isUserInRole("user"));

		context.setAttribute(AuthComponent.REMOTE_USER_KEY, "admin");
		request.addUserInRole("user");
		assertEquals(true, request.isUserInRole("user"));
		assertEquals(false, request.isUserInRole("test"));
	}

	@Test
	public void testGetAttribute() {
		request.setAttribute("name", "admin");
		assertEquals("admin", request.getAttribute("name"));
	}

	@Test
	public void testGetAttributeNames() {
		request.setAttribute("name", "admin");
		request.setAttribute("test", "123");
		//long start = System.nanoTime();
		Enumeration<?> names = request.getAttributeNames();
		//System.out.println(System.nanoTime() - start);
		assertEquals("name", names.nextElement());
		assertEquals("test", names.nextElement());
	}

	@Test
	public void testGetCharacterEncoding() {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			fail();
		}
		assertEquals("utf-8", request.getCharacterEncoding());
	}

	@Test
	public void testGetContentLength() {
		assertEquals(-1, request.getContentLength());
	}

	@Test
	public void testGetContentType() {
		req.addHeader("Content-Type","text/html");
		assertEquals("text/html", request.getContentType());
	}

	@Test
	public void testGetInputStream() {
		ServletInputStream in = null;
		try {
			in = request.getInputStream();
		} catch (IOException e) {
			fail();
		}
		assertNull(in);
		//TODO add test case.
	}

	@Test
	public void testGetLocalAddr() {
		assertEquals(null, request.getLocalAddr());
		//TODO add test case.
	}

	@Test
	public void testGetLocalName() {
		assertEquals(null, request.getLocalName());
		//TODO add test case.
	}

	@Test
	public void testGetLocalPort() {
		assertEquals(-1, request.getLocalPort());
		//TODO add test case.
	}

	@Test
	public void testGetLocale() {
		req.addHeader("Accept-Language", "en");
		assertEquals("en", request.getLocale().getLanguage());
	}

	@Test
	public void testGetLocales() {
		req.addHeader("Accept-Language", "en");
		req.addHeader("Accept-Language", "ja");
		Enumeration<?> locales = request.getLocales();
		assertEquals("en", ((Locale)locales.nextElement()).getLanguage());
		assertEquals("ja", ((Locale)locales.nextElement()).getLanguage());
	}

	@Test
	public void testGetParameter() {
		request.setParameter("a", "a");
		request.setParameter("b", "b");
		request.setParameter("c", "c");
		request.setParameter("p", "a");
		request.setParameter("p", "b");
		request.setParameter("p", "c");
		
		assertEquals("a", request.getParameter("a"));
		assertEquals("b", request.getParameter("b"));
		assertEquals("c", request.getParameter("c"));
		
		assertEquals("a", request.getParameter("p"));
	}

	@Test
	public void testGetParameterMap() {
		//req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user&p=a&p=b&p=c");
		request.setParameter("p", "a");
		request.setParameter("p", "b");
		request.setParameter("p", "c");
		request.setParameter("a", "a");
		request.setParameter("b", "b");
		
		Map<?,?> map = request.getParameterMap();
		String[] values = (String[])map.get("p");
		
		assertNotNull(values);
		assertEquals(3, values.length);
		assertEquals("a", values[0]);
		assertEquals("b", values[1]);
		assertEquals("c", values[2]);
		
		String[] valuesA = (String[])map.get("a");
		assertNotNull(valuesA);
		assertEquals(1, valuesA.length);
		assertEquals("a", valuesA[0]);
		
		String[] valuesB = (String[])map.get("b");
		assertNotNull(valuesB);
		assertEquals(1, valuesB.length);
		assertEquals("b", valuesB[0]);
		
		String[] valuesZ = (String[])map.get("z");
		assertNull(valuesZ);
	}

	@Test
	public void testGetParameterNames() {
		request.setParameter("a", "a");
		request.setParameter("b", "b");
		request.setParameter("c", "c");
		request.setParameter("p", "a");
		request.setParameter("p", "b");
		request.setParameter("p", "c");
		
		Enumeration<?> names = request.getParameterNames();
		assertNotNull(names);
		assertEquals("id", names.nextElement());
		assertEquals("type", names.nextElement());
		assertEquals("a", names.nextElement());
		assertEquals("b", names.nextElement());
		assertEquals("c", names.nextElement());
		assertEquals("p", names.nextElement());
	}

	@Test
	public void testGetParameterValues() {
		request.setParameter("a", "a");
		request.setParameter("b", "b");
		request.setParameter("c", "c");
		request.setParameter("p", "a");
		request.setParameter("p", "b");
		request.setParameter("p", "c");
		
		String[] values = request.getParameterValues("p");
		assertNotNull(values);
		assertEquals("a", values[0]);
		assertEquals("b", values[1]);
		assertEquals("c", values[2]);
		
		String[] valuesA = request.getParameterValues("a");
		assertNotNull(valuesA);
		assertEquals(1, valuesA.length);
		assertEquals("a", valuesA[0]);
		
		String[] valuesB = request.getParameterValues("b");
		assertNotNull(valuesB);
		assertEquals(1, valuesB.length);
		assertEquals("b", valuesB[0]);
		
		String[] valuesZ = request.getParameterValues("z");
		assertNull(valuesZ);
	}

	@Test
	public void testGetProtocol() {
		assertEquals("HTTP/1.1", request.getProtocol());
	}

	@Test
	public void testGetReader() {
		BufferedReader reader = null;
		try {
			reader = request.getReader();
		} catch (IOException e) {
			fail();
		}
		assertNull(reader);
		//TODO add test case.
	}

	@Test
	public void testGetRealPath() throws Exception {
		assertEquals(new File(System.getProperty("user.dir")
				  + "/src/test/resources/test/WEB-INF/web.xml").getCanonicalPath(),
				request.getRealPath("WEB-INF/web.xml"));
			
			assertEquals(new File(System.getProperty("user.dir")
				+ "/src/test/resources/test/WEB-INF/web.xml").getCanonicalPath(),
				request.getRealPath("/WEB-INF/web.xml"));
			
			//invalid path.("../") -> returns null
			assertNull(request.getRealPath("../../WEB-INF/web.xml"));
	}

	@Test
	public void testGetRemoteAddr() {
		assertEquals(null, request.getRemoteAddr());
		//TODO add test case.
	}

	@Test
	public void testGetRemoteHost() {
		assertEquals(null, request.getRemoteHost());
		//TODO add test case.
	}

	@Test
	public void testGetRemotePort() {
		assertEquals(-1, request.getRemotePort());
		//TODO add test case.
	}

	@Test
	public void testGetRequestDispatcher() {
		assertNotNull(request.getRequestDispatcher("test.jsp"));
		//TODO add test case.
	}

	@Test
	public void testGetScheme() {
		assertEquals("http", request.getScheme());
	}

	@Test
	public void testGetServerName() {
		req.addHeader("Host", "tamacat.org");
		assertEquals("tamacat.org", request.getServerName());
	}

	@Test
	public void testGetServerPort() {
		assertEquals(80, request.getServerPort());
	}

	@Test
	public void testIsSecure() {
		assertEquals(false, request.isSecure());
	}

	@Test
	public void testRemoveAttribute() {
		request.setAttribute("test", "abc");
		assertEquals("abc", request.getAttribute("test"));
		request.removeAttribute("test");
		assertEquals(null, request.getAttribute("test"));
	}

	@Test
	public void testSetAttribute() {
		request.setAttribute("test", "abc");
		assertEquals("abc", request.getAttribute("test"));
	}

	@Test
	public void testSetCharacterEncoding() {
		try {
			request.setCharacterEncoding("utf-8");
			
			assertEquals("utf-8", request.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			fail();
		}
	}
}
