/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.filter.acl.AccessUrlCache;
import org.tamacat.util.StringUtils;

/**
 * URL Based ACCESS CONTROL.
 */
public abstract class AbstractAccessControlFilter implements RequestFilter {
	
	protected AccessUrlCache cache;
	
	private int cacheSize = 100;
	private long cacheExpire = 30000;
	
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public void setCacheExpire(long cacheExpire) {
		this.cacheExpire = cacheExpire;
	}
	
	@Override
	public void init() {
		if (cacheSize > 0 && cacheExpire > 0) {
			cache = new AccessUrlCache(cacheSize, cacheExpire);
		}
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		String remoteUser = (String) context.getAttribute(remoteUserKey);
        if (remoteUser != null && serviceUrl != null) {
        	String accessUrl = null;
        	if (serviceUrl.isType(ServiceUrl.Type.REVERSE)) {
        		ReverseUrl reverseUrl = serviceUrl.getReverseUrl();
        		accessUrl = reverseUrl.getPath();
        	} else {
        		accessUrl = serviceUrl.getPath();
        	}
        	if (StringUtils.isEmpty(accessUrl)) throw new ForbiddenException();
        	
        	if (isSuccess(accessUrl) == false) {
        		throw new ForbiddenException();
        	}
        }
	}
	
	protected abstract boolean isSuccess(String url);
	
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
}
