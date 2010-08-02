/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

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

/**
 * <p>Implements of HTML Form based authentication.
 */
public class FormAuthProcessor extends AbstractAuthProcessor implements RequestFilter, ResponseFilter {

	protected static final String SC_UNAUTHORIZED
		= FormAuthProcessor.class.getName() + ".SC_UNAUTHORIZED";
	protected static final String SC_AUTHORIZED
		= FormAuthProcessor.class.getName() + ".SC_AUTHORIZED";
	
	protected String loginPageUrl = "login.html";
	protected String loginActionUrl = "check.html";
	protected String logoutActionUrl = "logout.html";
	protected String topPageUrl = "index.html";
	protected String usernameKey = "username";
	protected String passwordKey = "password";
	protected String sessionCookieName = "Session";
	protected String sessionUsernameKey = "SingleSignOnUser";
	protected Set<String> freeAccessExtensions = new HashSet<String>();

	public void setLoginPageUrl(String loginPageUrl) {
		this.loginPageUrl = loginPageUrl;
	}

	public void setLoginActionUrl(String loginActionUrl) {
		this.loginActionUrl = loginActionUrl;
	}
	
	public void setLogoutActionUrl(String logoutActionUrl) {
		this.logoutActionUrl = logoutActionUrl;
	}

	public void setTopPageUrl(String topPageUrl) {
		this.topPageUrl = topPageUrl;
	}

	public void setUsernameKey(String usernameKey) {
		this.usernameKey = usernameKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}
	
	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public void setSessionUsernameKey(String sessionUsernameKey) {
		this.sessionUsernameKey = sessionUsernameKey;
	}

	protected boolean isFreeAccessExtensions(String uri) {
		int idx = uri.lastIndexOf(".");
		if (idx >= 0) {
			String ext = uri.substring(idx+1, uri.length()).toLowerCase().trim();
			return freeAccessExtensions.contains(ext);
		} else {
			return false;
		}
	}

	/**
	 * <p>The extension skipping by the certification in comma seperated values.
	 * @param freeAccessExtensions (CSV)
	 */
	public void setFreeAccessExtensions(String freeAccessExtensions) {
		String[] list = freeAccessExtensions.split(",");
		for (String ext : list) {
			this.freeAccessExtensions.add(ext.trim().replaceFirst("^\\.", "").toLowerCase());
		}
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		//Get the session ID in client Cookie.
		String sessionId = HeaderUtils.getCookieValue(request, sessionCookieName);
		try {
			String remoteUser = null;
			String uri = request.getRequestLine().getUri();
			if (uri.endsWith(loginPageUrl) || isFreeAccessExtensions(uri)) {
				return; //skip by this filter.
			} else if (request.getRequestLine().getUri().endsWith(loginActionUrl)) {
				//login check
				remoteUser = checkUser(request, context);
				context.setAttribute(remoteUserKey, remoteUser);
				Session session = SessionManager.getInstance().createSession();
				session.setAttribute(sessionUsernameKey, remoteUser);
				response.setHeader("Set-Cookie", sessionCookieName + "=" + session.getId() + "; Path=/");
				context.setAttribute(SC_AUTHORIZED, Boolean.TRUE);
			} else if (StringUtils.isNotEmpty(sessionId)) {
				//already login. -> session check
				Session session = SessionManager.getInstance().getSession(sessionId);
				remoteUser = (String) session.getAttribute(sessionUsernameKey);
				if (remoteUser == null) { //invalid session.
					throw new UnauthorizedException();
				} else if (uri.endsWith(logoutActionUrl)) {
					//logout -> session delete -> login page.
					logoutAction(sessionId);
					context.setAttribute(SC_UNAUTHORIZED, Boolean.TRUE);
				}
			} else { //It does not yet login.
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
			//unauthorized -> Go to the login page.
			sendRedirect(response, loginPageUrl);
		} else if (Boolean.TRUE.equals(context.getAttribute(SC_AUTHORIZED))) {
			//authorized login -> Go to the top page.
			sendRedirect(response, topPageUrl);
		}
	}
	
	/**
	 * <p>Redirect for login action.
	 * @param response
	 * @param uri redirect URI path.
	 */
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
	
	/**
	 * <p>Logout the system with invalidate this session.
	 * @param sessionId
	 */
	protected void logoutAction(String sessionId) {
		Session session = SessionManager.getInstance().getSession(sessionId);
		if (session != null) session.invalidate();
	}
	
	/**
	 * login check with AuthComponent.
	 * @param request
	 * @param context
	 * @return login username in request parameter.
	 * @throws UnauthorizedException
	 */
	protected String checkUser(HttpRequest request, HttpContext context)
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