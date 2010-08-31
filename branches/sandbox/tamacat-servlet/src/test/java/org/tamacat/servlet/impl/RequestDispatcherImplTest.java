package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import java.net.URL;

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
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.ServletHttpHandler;
import org.tamacat.servlet.test.SampleServlet;

public class RequestDispatcherImplTest {

	HttpRequest req;
	HttpResponse res;
	ServiceUrl serviceUrl;
	
	HttpServletRequestImpl request;
	HttpServletResponseImpl response;
	RequestDispatcherImpl dispatcher;
	
	@Before
	public void setUp() throws Exception {
		HttpContext context = new BasicHttpContext();

		req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user");
		res = new DefaultHttpResponseFactory().newHttpResponse(
				new BasicStatusLine(
					new ProtocolVersion("HTTP", 1,1), 200,"OK"), context);
		
		ServiceConfigParser parser = new ServiceConfigParser(new ServerConfig());
		serviceUrl = parser.getConfig().getDefaultServiceConfig().getServiceUrl("/test/");
		serviceUrl.setHost(new URL("http://localhost"));
		ServletContextImpl servletContext = new ServletContextImpl(
				System.getProperty("user.dir")
				+ "/src/test/resources/test", serviceUrl);
		
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setServlet(new SampleServlet());
		servletUrl.setUrlPattern("/index.html");
		servletUrl.setServletName("SampleServlet");
		
		request = (HttpServletRequestImpl)
			new HttpServletObjectFactory(servletContext)
				.createRequest(servletUrl, req, context);
		response = (HttpServletResponseImpl)
			new HttpServletObjectFactory(servletContext)
				.createResponse(res, new BasicHttpContext());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testForward() throws Exception {
		ServletHttpHandler engine = new ServletHttpHandler();
		engine.setClassLoader(getClass().getClassLoader());
		engine.setServiceUrl(serviceUrl);
		
		dispatcher = new RequestDispatcherImpl(engine);
		dispatcher.forward(request, response);
		assertNotNull(dispatcher);
	}

	@Test
	public void testInclude() throws Exception {
		ServletHttpHandler engine = new ServletHttpHandler();
		engine.setClassLoader(getClass().getClassLoader());
		engine.setServiceUrl(serviceUrl);
		
		dispatcher = new RequestDispatcherImpl(engine);
		dispatcher.include(request, response);
		assertNotNull(dispatcher);
	}
}
