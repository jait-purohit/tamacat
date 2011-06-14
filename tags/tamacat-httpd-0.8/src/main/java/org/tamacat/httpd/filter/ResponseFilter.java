/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface ResponseFilter extends HttpFilter {
	
	void afterResponse(HttpRequest request, HttpResponse response, 
		HttpContext context);
}
