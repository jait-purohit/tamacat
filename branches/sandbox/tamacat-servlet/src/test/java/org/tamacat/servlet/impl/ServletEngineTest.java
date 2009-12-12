package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;

public class ServletEngineTest {

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
		ServletEngine engine = new ServletEngine(serviceUrl); 
		try {
			engine.processServlet("test1", req, null);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetServlet() {
		ServletEngine engine = new ServletEngine(serviceUrl); 
		Servlet servlet = engine.getServlet("test1");
		assertNotNull(servlet);
	}
}
