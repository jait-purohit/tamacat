/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.action;

import org.apache.http.HttpRequest;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;

public class DefaultAction implements Action {

	protected HttpContext context;
	protected HttpRequest request;
	protected HttpResponse response;
	
	@Override
	public void setContext(HttpContext context) {
		this.context = context;
	}

	@Override
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	@Override
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	protected String getRemoteUser() {
		return (String) context.getAttribute(AuthComponent.REMOTE_USER_KEY);
	}
}
