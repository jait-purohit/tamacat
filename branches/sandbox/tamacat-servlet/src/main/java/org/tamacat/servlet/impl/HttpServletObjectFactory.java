package org.tamacat.servlet.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;

public class HttpServletObjectFactory {
	
	private HttpCoreServletContext servletContext;
	
	public HttpServletObjectFactory(HttpCoreServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public HttpCoreServletContext getServletContext(ServiceUrl serviceUrl) {
		return servletContext;
	}
	
	public HttpCoreServletRequest createRequest(ServletUrl servletUrl, HttpRequest request, HttpContext context) {
		return new HttpServletRequestImpl(servletContext, servletUrl, request, context);
	}
	
	public HttpCoreServletResponse createResponse(HttpResponse response, HttpContext context) {
		return new HttpServletResponseImpl(servletContext, response, context);
	}
}