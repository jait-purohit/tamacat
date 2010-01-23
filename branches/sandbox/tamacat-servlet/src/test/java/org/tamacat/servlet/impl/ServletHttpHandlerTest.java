package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServlet;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;

public class ServletHttpHandlerTest {

	ServiceUrl serviceUrl;
	HttpRequest req;
	
	@Before
	public void setUp() throws Exception {
		req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user");
		ServiceConfigXmlParser parser
			= new ServiceConfigXmlParser(new ServerConfig());
		ServiceConfig serviceConfig = parser.getServiceConfig();
		serviceUrl = serviceConfig.getServiceUrl("/test/");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testServletEngine() {
		ServletHttpHandler engine = new ServletHttpHandler();
		engine.setServiceUrl(serviceUrl);
		try {
			engine.handle(req, null, new BasicHttpContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetServletFromName() {
		ServletHttpHandler engine = new ServletHttpHandler();
		engine.setServiceUrl(serviceUrl);

		HttpServlet servlet = engine.getServletFromName("test1");
		assertNotNull(servlet);
	}
	
	@Test
	public void testGetServletFromPath() {
		ServletHttpHandler engine = new ServletHttpHandler(); 
		engine.setServiceUrl(serviceUrl);

		HttpServlet servlet = engine.getServlet("/test1/test.html");
		assertNotNull(servlet);
		
		HttpServlet servlet2 = engine.getServlet("/test2/test.html");
		assertNotNull(servlet2);
		
		HttpServlet servlet3 = engine.getServlet("/test/test.do");
		assertNotNull(servlet3);
		
		HttpServlet nullServlet = engine.getServlet("/test/");
		assertNull(nullServlet);
	}
}
