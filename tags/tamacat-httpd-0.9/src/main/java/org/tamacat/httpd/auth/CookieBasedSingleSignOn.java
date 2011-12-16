/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * This class implements Single Sign-On with cookie.
 */
public class CookieBasedSingleSignOn implements SingleSignOn {

	static final Log LOG = LogFactory.getLog(CookieBasedSingleSignOn.class);
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	
	protected String singleSignOnCookieName;
	protected Set<String> freeAccessExtensions = new HashSet<String>();

	/**
	 * Constructor with Single Sign-On cookie.
	 * @param singleSignOnCookieName
	 */
	public CookieBasedSingleSignOn(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
	
	/**
	 * Default Constructor.
	 * default cookie name: "SingleSignOnUser"
	 */
	public CookieBasedSingleSignOn() {
		this.singleSignOnCookieName = "SingleSignOnUser";
	}
	
	/**
	 * Set the remote user key name. (optional)
	 * @param remoteUserKey
	 */
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
	
	/**
	 * Set the Single Sign-On cookie name. 
	 * default: "SingleSignOnUser"
	 * @param singleSignOnCookieName
	 */
	public void setSingleSignOnCookieName(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
	
	/**
	 * Whether it agrees to the extension that can be accessed
	 * without the attestation is inspected.  
	 * @param uri
	 * @return true: contains the freeAccessExtensions.
	 */
	protected boolean isFreeAccessExtensions(String uri) {
		if (freeAccessExtensions.size() > 0) {
			int idx = uri.lastIndexOf(".");
			if (idx >= 0) {
				String ext = uri.substring(idx+1, uri.length()).toLowerCase().trim();
				return freeAccessExtensions.contains(ext);
			}
		}
		return false;
	}

	/**
	 * The extension skipping by the certification in comma seperated values.
	 * @param freeAccessExtensions (CSV)
	 */
	public void setFreeAccessExtensions(String freeAccessExtensions) {
		String[] list = freeAccessExtensions.split(",");
		for (String ext : list) {
			this.freeAccessExtensions.add(ext.trim().replaceFirst("^\\.", "").toLowerCase());
		}
	}
	
	@Override
	public String getSignedUser(HttpRequest request, HttpContext context) {
		String remoteUser = (String) context.getAttribute(remoteUserKey);
		if (StringUtils.isNotEmpty(remoteUser)) {
			return remoteUser;
		} else {
			Header[] cookieHeaders = request.getHeaders("Cookie");
			String user = null;
			for (Header h : cookieHeaders) {
				String cookie = h.getValue();
				user = HeaderUtils.getCookieValue(cookie, singleSignOnCookieName);
				LOG.trace("CookieUser: " + user);
				if (StringUtils.isNotEmpty(user)) {
					return user;
				}
			}
		}
		throw new UnauthorizedException();
	}
	
	@Override
	public boolean isSigned(HttpRequest request, HttpContext context) {
		String user = getSignedUser(request, context);
		if (StringUtils.isNotEmpty(user)) {
			return true;
		}
		String path = RequestUtils.getRequestPath(request);
		if (isFreeAccessExtensions(path)) {
			return true;
		}
		return false;
	}
	
	@Override
	public void sign(String remoteUser, HttpRequest request, HttpResponse response, HttpContext context) {
		if (StringUtils.isNotEmpty(remoteUser)) {
			response.setHeader("Set-Cookie", singleSignOnCookieName + "=" + remoteUser + "; Path=/");
			request.setHeader("Cookie",	singleSignOnCookieName + "=" + remoteUser); //for Reverse Proxy
			LOG.trace("Set-Cookie: " + singleSignOnCookieName + "=" + remoteUser + "; Path=/");
		}
	}
}
