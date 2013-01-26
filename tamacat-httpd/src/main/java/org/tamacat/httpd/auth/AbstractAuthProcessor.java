/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.util.StringUtils;

/**
 * The abstract class of authentication processor.
 */
public abstract class AbstractAuthProcessor implements RequestFilter,
		ResponseFilter {

	protected AuthComponent<?> authComponent;
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	protected ServiceUrl serviceUrl;
	protected SingleSignOn singleSignOn;
	protected String algorithmName; // ex. SHA-256

	protected Set<String> freeAccessExtensions = new HashSet<String>();

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		if (authComponent != null) {
			authComponent.init();
		}
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if (authComponent != null) {
			authComponent.release();
		}
	}

	/**
	 * Set the {@link AuthComponent}. (required)
	 * 
	 * @param authComponent
	 */
	public void setAuthComponent(AuthComponent<?> authComponent) {
		this.authComponent = authComponent;
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
	 * Get the Key name of Remote user.
	 * 
	 * @return remoteUserKey
	 */
	public String getRemoteUserKey() {
		return remoteUserKey;
	}

	/**
	 * Set the SingleSignOn object.
	 * 
	 * @param singleSignOn
	 */
	public void setSingleSignOn(SingleSignOn singleSignOn) {
		this.singleSignOn = singleSignOn;
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

	/**
	 * Set the encryption algorithm for "getEncriptedPassword" method. ex.
	 * "SHA-256"
	 * 
	 * @param algorithmName
	 */
	public void setPasswordEncryptionAlgorithm(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * Get the encrypted password. Please set up the encryption algorithm by a
	 * "setPasswordEncryptedAlgorithm" method in advance. if "algorithmName" is
	 * empty then returns a plain password.
	 * 
	 * @param password
	 *            (Plain password)
	 * @return encrypted password or plain password(algorithm is empty)
	 */
	protected String getEncryptedPassword(String password) {
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(algorithmName)) {
			return password;
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algorithmName);
			md.update(password.getBytes());
			byte[] digest = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				String hex = String.format("%02x", b);
				sb.append(hex);
			}
			return sb.toString();
		} catch (Exception e) {
			throw new UnauthorizedException();
		}
	}
}
