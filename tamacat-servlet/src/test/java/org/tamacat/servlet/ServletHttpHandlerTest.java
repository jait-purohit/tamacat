package org.tamacat.servlet;

import static org.junit.Assert.*;

import java.net.URL;

import javax.servlet.http.HttpServlet;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.ServletHttpHandler;
import org.tamacat.servlet.impl.ServletUrl;

public class ServletHttpHandlerTest {

	ServiceUrl serviceUrl;
	HttpRequest req;
	HttpResponse res;
	
	@Before
	public void setUp() throws Exception {
		req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user");
		ServiceConfigParser parser
			= new ServiceConfigParser(new ServerConfig());
		ServiceConfig serviceConfig = parser.getConfig().getDefaultServiceConfig();
		serviceUrl = serviceConfig.getServiceUrl("/test/");
		serviceUrl.setHost(new URL("http://localhost"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testServletEngine() {
		ServletHttpHandler engine = new ServletHttpHandler();
		engine.setClassLoader(getClass().getClassLoader());
		engine.setServiceUrl(serviceUrl);
		try {
			HttpContext context =new BasicHttpContext();
			res = new DefaultHttpResponseFactory().newHttpResponse(
					new BasicStatusLine(
						new ProtocolVersion("HTTP", 1,1), 200,"OK"), context);
			engine.handle(req, res, context);
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

		HttpServlet servlet = engine.getServletUrl("/test1/test.html").getServlet();
		assertNotNull(servlet);
		
		HttpServlet servlet2 = engine.getServletUrl("/test2/test.html").getServlet();
		assertNotNull(servlet2);
		
		HttpServlet servlet3 = engine.getServletUrl("/test/test.do").getServlet();
		assertNotNull(servlet3);
		
		ServletUrl nullServlet = engine.getServletUrl("/test/");
		assertNull(nullServlet);
	}
}