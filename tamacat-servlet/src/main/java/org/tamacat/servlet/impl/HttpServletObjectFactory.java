package org.tamacat.servlet.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;

public class HttpServletObjectFactory {
	
	private ServiceUrl serviceUrl;
	
	HttpServletObjectFactory(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public ServletContext createServletContext(ServiceUrl serviceUrl) {
		return new ServletContextImpl(serviceUrl);
	}
	
	public HttpServletRequest createRequest(HttpRequest request, HttpContext context) {
		return new HttpServletRequestImpl(serviceUrl, request, context);
	}
}
