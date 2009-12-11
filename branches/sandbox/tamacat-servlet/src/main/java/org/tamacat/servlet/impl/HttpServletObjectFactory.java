package org.tamacat.servlet.impl;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletRequest;

public class HttpServletObjectFactory {
	
	private HttpCoreServletContext servletContext;
	
	HttpServletObjectFactory(ServiceUrl serviceUrl) {
		this.servletContext = new ServletContextImpl(serviceUrl);
	}
	
	public HttpCoreServletContext getServletContext(ServiceUrl serviceUrl) {
		return servletContext;
	}
	
	public HttpCoreServletRequest createRequest(HttpRequest request, HttpContext context) {
		return new HttpServletRequestImpl(servletContext, request, context);
	}
}
