/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.httpd.util.StringUtils;


public class SingleSignOnFilter implements RequestFilter {

	static final Log LOG = LogFactory.getLog(SingleSignOnFilter.class);

	protected String singleSignOnCookieName;
	private String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	
	public SingleSignOnFilter(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
	
	public SingleSignOnFilter() {
		this.singleSignOnCookieName = "SingleSignOnUser";
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		String remoteUser = (String) context.getAttribute(remoteUserKey);
		if (StringUtils.isNotEmpty(remoteUser)) {
			Header[] cookieHeaders = request.getHeaders("Cookie");
			String user = null;
			for (Header h : cookieHeaders) {
				String cookie = h.getValue();
				user = HeaderUtils.getCookieValue(cookie, singleSignOnCookieName);
				LOG.trace("CookieUser: " + user);
				if (StringUtils.isNotEmpty(user)) {
					break;
				}
			}
			if (StringUtils.isEmpty(user)) {
				response.addHeader("Set-Cookie",
					singleSignOnCookieName + "=" + remoteUser + "; Path=/");
				LOG.trace("Set-Cookie: " + singleSignOnCookieName + "=" + remoteUser);
			}
		} else {
			throw new UnauthorizedException();
		}
	}

	public void setSingleSignOnCookieName(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
	
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
}
