/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.StringUtils;

public class RequestContext {

	private final HttpRequest request;
	private final HttpResponse response;
	private final HttpContext context;
	
	public RequestContext(
			HttpRequest request, HttpResponse response, HttpContext context) {
		this.request = request;
		this.response = response;
		this.context = context;
	}
	
	public HttpRequest getRequest() {
		return request;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public HttpContext getContext() {
		return context;
	}
	
	public String getParameter(String name) {
		return RequestUtils.getParameter(context, name);
	}
	
	public <T>T getParameter(String name, T defaultValue) {
		String value = RequestUtils.getParameter(context, name);
		return StringUtils.parse(value, defaultValue);
	}
	
	public String[] getParameters(String name) {
		return RequestUtils.getParameters(context, name);
	}
	
	public void setAttribute(String name, Object value) {
		context.setAttribute(name, value);
	}
	
	public Object getAttribute(String name) {
		return context.getAttribute(name);
	}
}
