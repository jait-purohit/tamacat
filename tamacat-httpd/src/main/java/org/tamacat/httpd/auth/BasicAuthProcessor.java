/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.util.StringUtils;

/**
 * <p>Implements of Basic authentication.
 */
public class BasicAuthProcessor extends AbstractAuthProcessor implements RequestFilter {

	static final String AUTHORIZATION = "Authorization";
	static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	protected String realm = "Authentication required";
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
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
		this.realm = DynamicRealm.getRealm(realm, new Date());
	}
}
