/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;


public class SingleSignOnFilter implements RequestFilter {

	static final Log LOG = LogFactory.getLog(SingleSignOnFilter.class);

	protected ServiceUrl serviceUrl;
	protected String singleSignOnCookieName;
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	
	public SingleSignOnFilter(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
	
	public SingleSignOnFilter() {
		this.singleSignOnCookieName = "SingleSignOnUser";
	}
	
	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
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
				response.setHeader("Set-Cookie", singleSignOnCookieName + "=" + remoteUser + "; Path=/");
				request.setHeader("Cookie",	singleSignOnCookieName + "=" + remoteUser); //for Reverse Proxy
				LOG.trace("Set-Cookie: " + singleSignOnCookieName + "=" + remoteUser + "; Path=/");
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
