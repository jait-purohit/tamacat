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
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.StringUtils;

/**
 * Implements of Basic authentication.
 */
public class BasicAuthProcessor extends AbstractAuthProcessor {

	static final String AUTHORIZATION = "Authorization";
	static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	/** default realm. */
	protected String realm = "Authentication required";
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		String path = RequestUtils.getRequestPath(request);
		if (isFreeAccessExtensions(path) == false) {
			try {
				String remoteUser = checkUser(request, context);
				context.setAttribute(remoteUserKey, remoteUser);
			} catch (UnauthorizedException e) {
				response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
				setWWWAuthenticateHeader(response);
				throw e;
			}
		}
	}

	/**
	 * Set the "WWW-Authenticate" response header of Basic authenticate realm.
	 * @param response
	 */
	protected void setWWWAuthenticateHeader(HttpResponse response) {
		response.addHeader(WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
	}

	/**
	 * Realm is changed.
	 * Default realm is "Authentication required".
	 * @param realm
	 */
	public void setRealm(String realm) {
		this.realm = DynamicRealm.getRealm(realm, new Date());
	}
	
	/**
	 * When the user authentication check and correct,
	 * the username(login id) is returned. 
	 * @param request
	 * @param context
	 * @return username (login id)
	 * @throws UnauthorizedException
	 */
	protected String checkUser(HttpRequest request, HttpContext context)
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
}
