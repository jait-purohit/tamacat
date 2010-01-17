package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.Servlet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.test.TestServlet;

public class ServletContextImplTest {
	
	ServiceUrl serviceUrl;
	ServletContextImpl context;
	
	@Before
	public void setUp() throws Exception {
		ServiceConfigXmlParser parser = new ServiceConfigXmlParser(new ServerConfig());
		serviceUrl = parser.getServiceConfig().getServiceUrl("/test/");
		context = new ServletContextImpl(
			System.getProperty("user.dir")
			+ "/src/test/resources/test", serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddServlet() throws Exception {
		context.addServlet("test", new TestServlet());
		Servlet servlet = context.getServlet("test");
		assertEquals("TestServlet", servlet.getServletInfo());
	}

	@Test
	public void testRemoveServlet() throws Exception {
		context.addServlet("test", new TestServlet());
		Servlet servlet = context.getServlet("test");
		assertEquals("TestServlet", servlet.getServletInfo());
		
		context.removeServlet("test");
		assertNull(context.getServlet("test"));
	}

	@Test
	public void testGetServiceUrl() {
		ServiceUrl get = context.getServiceUrl();
		assertEquals(get, serviceUrl);
	}

	@Test
	public void testGetAttribute() {
		context.setAttribute("name", "test");
		assertEquals("test", context.getAttribute("name"));
	}

	@Test
	public void testRemoveAttribute() {
		context.setAttribute("name", "test");
		assertEquals("test", context.getAttribute("name"));
		
		context.removeAttribute("name");
		assertNull(context.getAttribute("name"));
		try {
			context.removeAttribute("null");
			context.removeAttribute(null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testSetAttribute() {
		context.setAttribute("name1", "test1");
		context.setAttribute("name2", "test2");
		context.setAttribute("name3", "test3");

		assertEquals("test1", context.getAttribute("name1"));
		assertEquals("test2", context.getAttribute("name2"));
		assertEquals("test3", context.getAttribute("name3"));
		assertNull(context.getAttribute("name"));
	}

	@Test
	public void testGetAttributeNames() {
		context.setAttribute("name1", "test1");
		context.setAttribute("name2", "test2");
		context.setAttribute("name3", "test3");
		
		Enumeration<?> names = context.getAttributeNames();
		assertEquals("name1", names.nextElement());
		assertEquals("name2", names.nextElement());
		assertEquals("name3", names.nextElement());
		assertFalse(names.hasMoreElements());
	}

	@Test
	public void testGetContext() {
		assertNotNull(context.getContext("/test"));
		assertEquals(context, context.getContext("/test"));
	}

	@Test
	public void testGetContextPath() {
		assertNotNull(context.getContextPath());
		assertEquals("/test", context.getContextPath());
	}

	@Test
	public void testGetInitParameter() {
		context.addInitParam("name1", "test1");
		context.addInitParam("name2", "test2");
		context.addInitParam("name3", "test3");
		
		assertEquals("test1", context.getInitParameter("name1"));
		assertEquals("test2", context.getInitParameter("name2"));
		assertEquals("test3", context.getInitParameter("name3"));
		assertNull(context.getInitParameter("name0"));
	}

	@Test
	public void testGetInitParameterNames() {
		context.addInitParam("name1", "test1");
		context.addInitParam("name2", "test2");
		context.addInitParam("name3", "test3");
		
		Enumeration<?> names = context.getInitParameterNames();
		assertEquals("name1", names.nextElement());
		assertEquals("name2", names.nextElement());
		assertEquals("name3", names.nextElement());
		assertFalse(names.hasMoreElements());
	}

	@Test
	public void testGetMajorVersion() {
		assertEquals(2, context.getMajorVersion());
	}
	
	@Test
	public void testGetMimeType() {
		context.addMimeType("html", "text/html");
		context.addMimeType("txt", "text/plain");
		assertEquals("text/html", context.getMimeType("test.html"));
		assertEquals("text/plain", context.getMimeType("test.txt"));

		//MIME type is not known.
		assertNull(context.getMimeType("test"));
	}

	@Test
	public void testGetMinorVersion() {
		assertEquals(5, context.getMinorVersion());
	}
	
	@Test
	public void testGetNamedDispatcher() {
		context.addServlet("test", new TestServlet());
		assertNotNull(context.getNamedDispatcher("test"));
	}

	@Test
	public void testGetRealPath() throws Exception {
		
		assertEquals(new File(System.getProperty("user.dir")
			  + "/src/test/resources/test/WEB-INF/web.xml").getCanonicalPath(),
			context.getRealPath("WEB-INF/web.xml"));
		
		assertEquals(new File(System.getProperty("user.dir")
			+ "/src/test/resources/test/WEB-INF/web.xml").getCanonicalPath(),
			context.getRealPath("/WEB-INF/web.xml"));
		
		//invalid path.("../") -> returns null
		assertNull(context.getRealPath("../../WEB-INF/web.xml"));
	}

	@Test
	public void testGetRequestDispatcher() {
		assertNotNull(context.getRequestDispatcher("/top.html"));
	}

	@Test
	public void testGetResource() throws Exception {
		URL url = context.getResource("/index.html");
		assertNotNull(url);
	}

	@Test
	public void testGetResourceAsStream() throws Exception {
		InputStream in = context.getResourceAsStream("/index.html");
		assertNotNull(in);
		in.close();
	}

	@Test
	public void testGetResourcePaths() {
		Set<?> set = context.getResourcePaths("/");
		assertNotNull(set);
		for (Object path : set) {
			System.out.println(path);
		}
	}

	@Test
	public void testGetServerInfo() {
		context.setServerInfo("tamacat-servlet");
		assertEquals("tamacat-servlet", context.getServerInfo());
	}

	@Test
	public void testGetServlet() throws Exception {
		context.addServlet("test1", new TestServlet());
		context.addServlet("test2", new TestServlet());
		context.addServlet("test3", new TestServlet());
		
		Servlet servlet1 = context.getServlet("test1");
		assertEquals("TestServlet", servlet1.getServletInfo());
		
		Servlet servlet2 = context.getServlet("test2");
		assertEquals("TestServlet", servlet2.getServletInfo());
		
		Servlet servlet3 = context.getServlet("test3");
		assertEquals("TestServlet", servlet3.getServletInfo());
		
		Servlet servlet0 = context.getServlet("tes0");
		assertNull(servlet0);
	}

	@Test
	public void testGetServletContextName() {
		context.setServletContextName("Test Context");
		assertEquals("Test Context", context.getServletContextName());
	}

	@Test
	public void testGetServletNames() {
		context.addServlet("test1", new TestServlet());
		context.addServlet("test2", new TestServlet());
		context.addServlet("test3", new TestServlet());
		
		Enumeration<?> names = context.getServletNames();
		assertEquals("test1", names.nextElement());
		assertEquals("test2", names.nextElement());
		assertEquals("test3", names.nextElement());
		assertFalse(names.hasMoreElements());
	}

	@Test
	public void testGetServlets() {
		context.addServlet("test1", new TestServlet());
		context.addServlet("test2", new TestServlet());
		context.addServlet("test3", new TestServlet());
		
		Enumeration<?> servlets = context.getServlets();
		assertEquals("TestServlet", ((Servlet)servlets.nextElement()).getServletInfo());
		assertEquals("TestServlet", ((Servlet)servlets.nextElement()).getServletInfo());
		assertEquals("TestServlet", ((Servlet)servlets.nextElement()).getServletInfo());
		assertFalse(servlets.hasMoreElements());
	}

	@Test
	public void testLogString() {
		context.log("test message.");
	}

	@Test
	public void testLogExceptionString() {
		context.log(new RuntimeException("Test Exception."), "test message.");
	}

	@Test
	public void testLogStringThrowable() {
		context.log("test message.", new RuntimeException("Test Exception."));
	}

}
