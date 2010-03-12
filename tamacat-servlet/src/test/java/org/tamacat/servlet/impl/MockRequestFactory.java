package org.tamacat.servlet.impl;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.test.TestServlet;

public class MockRequestFactory {
	
	private String contextPath;
	HttpContext context;
	HttpRequest req;
	
	public MockRequestFactory(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public HttpCoreServletRequest createGetRequest(String url) {
		req = new BasicHttpRequest("GET", contextPath + url);
		context = new BasicHttpContext();

		ServiceConfigParser parser = new ServiceConfigParser(new ServerConfig());
		ServiceUrl serviceUrl = parser.getConfig().getDefaultServiceConfig().getServiceUrl(contextPath);
		
		ServletContextImpl servletContext = new ServletContextImpl(
				System.getProperty("user.dir")
				+ "/src/test/resources" + contextPath, serviceUrl);
		
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setServlet(new TestServlet());
		servletUrl.setUrlPattern("/index.html");
		servletUrl.setServletName("TestServlet");
		
		HttpServletRequestImpl request = (HttpServletRequestImpl)
			new HttpServletObjectFactory(servletContext)
					.createRequest(servletUrl, req, context);
		return request;
	}
	
	public HttpContext getHttpContext() {
		return context;
	}
	
	
	public HttpRequest getHttpRequest() {
		return req;
	}
}
