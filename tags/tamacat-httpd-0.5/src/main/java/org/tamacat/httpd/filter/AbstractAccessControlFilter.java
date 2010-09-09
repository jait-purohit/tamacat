/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.filter.acl.AccessUrlCache;
import org.tamacat.util.StringUtils;

/**
 * <p>The abstract class of URL based access control.
 */
public abstract class AbstractAccessControlFilter implements RequestFilter {
	
	protected AccessUrlCache cache;
	
	private int cacheSize = 100;
	private long cacheExpire = 30000;
	
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	
	/**
	 * <p>Set the maximum number of instances.
	 * @param cacheSize default 100 instances.
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	/**
	 * <p>Set the cache expire time (ms).<br>
	 * @param cacheExpire default 30000 ms. 
	 */
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
        	String accessUrl = serviceUrl.getPath();
//        	if (serviceUrl.isType(ServiceType.REVERSE)) {
//        		ReverseUrl reverseUrl = serviceUrl.getReverseUrl();
//        		accessUrl = reverseUrl.getServiceUrl().getPath();
//        	} else {
//        		accessUrl = serviceUrl.getPath();
//        	}
        	if (StringUtils.isEmpty(accessUrl)) throw new ForbiddenException();
        	
        	if (isSuccess(remoteUser, accessUrl) == false) {
        		throw new ForbiddenException();
        	}
        }
	}
	
	/**
	 * <p>Implements for sub class.<br>
	 * When the accessible URL returns true.
	 * @param username
	 * @param url
	 * @return true: accessible.
	 */
	protected abstract boolean isSuccess(String username, String url);
	
	/**
	 * <p>Set the remote user key.
	 * @param remoteUserKey
	 */
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
}