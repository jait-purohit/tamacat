/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.session.Session;
import org.tamacat.httpd.session.SessionManager;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.StringUtils;

public class FormAuthProcessor extends AbstractAuthProcessor implements RequestFilter, ResponseFilter {

	private static final String SC_UNAUTHORIZED
		= FormAuthProcessor.class.getName() + ".SC_UNAUTHORIZED";
	private static final String SC_AUTHORIZED
		= FormAuthProcessor.class.getName() + ".SC_AUTHORIZED";
	
	private String loginPageUrl = "login.html";
	private String loginActionUrl = "check.html";
	private String topPageUrl = "index.html";
	private String usernameKey = "username";
	private String passwordKey = "password";
	private String sessionCookieName = "Session";
	private String sessionUsernameKey = "SingleSignOnUser";
	 
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		String sessionId = HeaderUtils.getCookieValue(request, sessionCookieName);
		try {
			String remoteUser = null;
			String uri = request.getRequestLine().getUri();
			if (uri.endsWith(loginPageUrl)
				|| uri.endsWith(".css") || uri.endsWith(".js")) {
				return;
			}
			if (request.getRequestLine().getUri().endsWith(loginActionUrl)) {
				remoteUser = checkUser(request, context);
				context.setAttribute(remoteUserKey, remoteUser);
				Session session = SessionManager.getInstance().createSession();
				session.setAttribute(sessionUsernameKey, remoteUser);
				context.setAttribute(SC_AUTHORIZED, Boolean.TRUE);
				response.setHeader("Set-Cookie", sessionCookieName + "=" + session.getId() + "; Path=/");
			} else if (StringUtils.isNotEmpty(sessionId)) {
				Session session = SessionManager.getInstance().getSession(sessionId);
				remoteUser = (String) session.getAttribute(sessionUsernameKey);
				if (remoteUser == null) {
					throw new UnauthorizedException();
				}
			} else {
				throw new UnauthorizedException();
			}
		} catch (UnauthorizedException e) {
			response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
			context.setAttribute(SC_UNAUTHORIZED, Boolean.TRUE);
		}
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if (Boolean.TRUE.equals(context.getAttribute(SC_UNAUTHORIZED))) {
			sendRedirect(response, loginPageUrl);
		} else if (Boolean.TRUE.equals(context.getAttribute(SC_AUTHORIZED))) {
			sendRedirect(response, topPageUrl);
		}
	}
	
	protected void sendRedirect(HttpResponse response, String uri) {
		try {
			response.setHeader(HTTP.CONTENT_TYPE, "text/html; charset=UTF-8");
			response.setEntity(new StringEntity(
				"<html><meta http-equiv=\"refresh\" content=\"0;url=" +
				uri + "\"></html>", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new UnauthorizedException();
		}
	}
	
	public String checkUser(HttpRequest request, HttpContext context)
			throws UnauthorizedException {
		String username = RequestUtils.getParameter(context, usernameKey);
		String password = RequestUtils.getParameter(context, passwordKey);
		if (StringUtils.isNotEmpty(username)) {
			if (authComponent != null
					&& authComponent.check(username, password, context)) {
				return username;
			}
		}
		throw new UnauthorizedException();
	}
}