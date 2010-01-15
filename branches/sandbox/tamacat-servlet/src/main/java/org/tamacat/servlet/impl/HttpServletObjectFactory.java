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
	
	HttpServletObjectFactory(HttpCoreServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public HttpCoreServletContext getServletContext(ServiceUrl serviceUrl) {
		return servletContext;
	}
	
	public HttpCoreServletRequest createRequest(HttpRequest request, HttpContext context) {
		return new HttpServletRequestImpl(servletContext, request, context);
	}
	
	public HttpCoreServletResponse createResponse(HttpResponse response, HttpContext context) {
		return new HttpServletResponseImpl(servletContext, response, context);
	}
}
