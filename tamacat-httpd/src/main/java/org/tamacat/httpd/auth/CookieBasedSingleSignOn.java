/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.tamacat.util.UniqueCodeGenerator;

/**
 * This class implements Single Sign-On with cookie.
 */
public class CookieBasedSingleSignOn implements SingleSignOn {

	static final Log LOG = LogFactory.getLog(CookieBasedSingleSignOn.class);
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;

	protected String singleSignOnCookieName = "SingleSignOnUser";
	protected String singleSignOnCookieMessageDigest = "SingleSignOnMessageDigest";
	protected String singleSignOnCookieNonce = "SingleSignOnNonce";
	protected String privateKey = UniqueCodeGenerator.generate();

	protected Set<String> freeAccessExtensions = new HashSet<>();

	/**
	 * Constructor with Single Sign-On cookie.
	 * 
	 * @param singleSignOnCookieName
	 */
	public CookieBasedSingleSignOn(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}

	/**
	 * Default Constructor. default cookie name: "SingleSignOnUser"
	 */
	public CookieBasedSingleSignOn() {
		this.singleSignOnCookieName = "SingleSignOnUser";
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * Set the remote user key name. (optional)
	 * 
	 * @param remoteUserKey
	 */
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}

	/**
	 * Set the Single Sign-On cookie name. default: "SingleSignOnUser"
	 * 
	 * @param singleSignOnCookieName
	 */
	public void setSingleSignOnCookieName(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}

	public void setSingleSignOnCookieMessageDigest(
			String singleSignOnCookieMessageDigest) {
		this.singleSignOnCookieMessageDigest = singleSignOnCookieMessageDigest;
	}

	public void setSingleSignOnCookieNonce(String singleSignOnCookieNonce) {
		this.singleSignOnCookieNonce = singleSignOnCookieNonce;
	}

	/**
	 * Whether it agrees to the extension that can be accessed without the
	 * attestation is inspected.
	 * 
	 * @param uri
	 * @return true: contains the freeAccessExtensions.
	 */
	protected boolean isFreeAccessExtensions(String uri) {
		if (freeAccessExtensions.size() > 0) {
			int idx = uri.lastIndexOf(".");
			if (idx >= 0) {
				String ext = uri.substring(idx + 1, uri.length()).toLowerCase().trim();
				return freeAccessExtensions.contains(ext);
			}
		}
		return false;
	}

	/**
	 * The extension skipping by the certification in comma seperated values.
	 * 
	 * @param freeAccessExtensions
	 *            (CSV)
	 */
	public void setFreeAccessExtensions(String freeAccessExtensions) {
		String[] list = freeAccessExtensions.split(",");
		for (String ext : list) {
			this.freeAccessExtensions.add(ext.trim().replaceFirst("^\\.", "").toLowerCase());
		}
	}

	@Override
	public String getSignedUser(HttpRequest request, HttpResponse response, HttpContext context) {
		Header[] cookieHeaders = request.getHeaders("Cookie");
		for (Header h : cookieHeaders) {
			String cookie = h.getValue();
			String user = HeaderUtils.getCookieValue(cookie, singleSignOnCookieName);
			String nonce = HeaderUtils.getCookieValue(cookie, singleSignOnCookieNonce);
			String md = HeaderUtils.getCookieValue(cookie, singleSignOnCookieMessageDigest);
			if (checkMessageDigest(user, nonce, md)) {
				LOG.trace("CookieUser: " + user);
				return user;
			}
		}
		throw new UnauthorizedException();
	}

	@Override
	public boolean isSigned(HttpRequest request, HttpResponse response, HttpContext context) {
		String user = getSignedUser(request, response, context);
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
			context.setAttribute(remoteUserKey, remoteUser);
			response.addHeader("Set-Cookie", singleSignOnCookieName + "=" + remoteUser + "; Path=/");
			// for Reverse Proxy
			request.addHeader("Cookie", singleSignOnCookieName + "=" + remoteUser);
			LOG.trace("Set-Cookie: " + singleSignOnCookieName + "=" + remoteUser + "; Path=/");

			String nonce = generateNonce();
			if (nonce != null) {
				response.addHeader("Set-Cookie", singleSignOnCookieNonce + "=" + nonce + "; Path=/");
				request.addHeader("Cookie", singleSignOnCookieNonce + "=" + nonce);
			}
			String md = getMessageDigest(remoteUser, nonce);
			if (md != null) {
				response.addHeader("Set-Cookie", singleSignOnCookieMessageDigest + "=" + md + "; Path=/");
				request.addHeader("Cookie", singleSignOnCookieMessageDigest + "=" + md);
			}
		}
	}

	@Override
	public void unsign(String remoteUser, HttpRequest request, HttpResponse response, HttpContext context) {
		if (StringUtils.isNotEmpty(remoteUser)) {
			context.removeAttribute(remoteUserKey);
			request.removeHeaders("Cookie"); // for Reverse Proxy
			response.addHeader("Set-Cookie", singleSignOnCookieName
					+ "=; Path=/; expires=Thu, 1-Jan-1970 00:00:00 GMT");
			response.addHeader("Set-Cookie", singleSignOnCookieNonce
					+ "=; Path=/; expires=Thu, 1-Jan-1970 00:00:00 GMT");
			response.addHeader("Set-Cookie", singleSignOnCookieMessageDigest
					+ "=; Path=/; expires=Thu, 1-Jan-1970 00:00:00 GMT");
		}
	}

	protected String getMessageDigest(String remoteUser, String nonce) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] md = digest.digest((remoteUser + ":" + nonce + ":" + privateKey).getBytes());
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < md.length; i++) {
				String tmp = Integer.toHexString(md[i] & 0xff);
				if (tmp.length() == 1) {
					buffer.append('0').append(tmp);
				} else {
					buffer.append(tmp);
				}
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	protected String generateNonce() {
		return UniqueCodeGenerator.generate();
	}

	protected boolean checkMessageDigest(String remoteUser, String nonce, String md) {
		return md != null && md.equals(getMessageDigest(remoteUser, nonce));
	}
}
