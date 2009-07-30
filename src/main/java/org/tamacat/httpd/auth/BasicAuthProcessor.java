/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.util.StringUtils;

/**
 * <p>BASIC AUTHENTICATION
 */
public class BasicAuthProcessor implements RequestFilter {

	static final String AUTHORIZATION = "Authorization";
	static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	protected String realm = "Authentication required";
	protected AuthComponent<?> authComponent;
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;

	@Override
	public void init() {
		if (authComponent != null) {
			authComponent.init();
		}
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		try {
			String remoteUser = checkUser(request, context);
			context.setAttribute(remoteUserKey, remoteUser);
		} catch (UnauthorizedException e) {
			response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
			setWWWAuthenticateHeader(response);
			throw e;
		}
	}

	public void setWWWAuthenticateHeader(HttpResponse response) {
		response.addHeader(WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
	}

	public String checkUser(HttpRequest request, HttpContext context)
			throws UnauthorizedException {
		Header basicAuthLine = request.getFirstHeader(AUTHORIZATION);
		if (basicAuthLine != null && StringUtils.isNotEmpty(basicAuthLine.getValue())) {
			String idpassBase64 = basicAuthLine.getValue().replaceFirst("Basic ", "");
			String idpass = new String(new Base64().decode(idpassBase64.getBytes()));
			int pos = idpass.indexOf(':');
			if (pos >= 0) {
				String user = idpass.substring(0, pos);
				String password = idpass.substring(pos + 1, idpass.length());
				if (authComponent != null
						&& authComponent.check(user, password, context)) {
					return user;
				}
			}
		}
		throw new UnauthorizedException();
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public void setAuthComponent(AuthComponent<?> authComponent) {
		this.authComponent = authComponent;
	}

	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
}
