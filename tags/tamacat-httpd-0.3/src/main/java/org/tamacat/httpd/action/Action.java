/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.action;

import org.apache.http.HttpRequest;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface Action {

	void setRequest(HttpRequest request);
	
	void setResponse(HttpResponse response);
	
	void setContext(HttpContext context);
	
}
