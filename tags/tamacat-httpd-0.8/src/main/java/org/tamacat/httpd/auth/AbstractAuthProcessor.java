/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;

/**
 * The abstract class of authentication processor.
 */
public abstract class AbstractAuthProcessor implements RequestFilter, ResponseFilter {

	protected AuthComponent<?> authComponent;
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	protected ServiceUrl serviceUrl;
	
	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		if (authComponent != null) {
			authComponent.init();
		}
	}
	
	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if (authComponent != null) {
			authComponent.release();
		}		
	}
	
	/**
	 * Set the {@link AuthComponent}. (required)
	 * @param authComponent
	 */
	public void setAuthComponent(AuthComponent<?> authComponent) {
		this.authComponent = authComponent;
	}

	/**
	 * Set the remote user key name. (optional)
	 * @param remoteUserKey
	 */
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
}
