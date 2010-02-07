/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;

/**
 * <p>{@core RequestFilter} is execute from
 * {@link HttpHandler#handleRequest} method.
 */
public interface RequestFilter extends HttpFilter {
	
	void doFilter(HttpRequest request, HttpResponse response, 
		HttpContext context, ServiceUrl serviceUrl);
}
