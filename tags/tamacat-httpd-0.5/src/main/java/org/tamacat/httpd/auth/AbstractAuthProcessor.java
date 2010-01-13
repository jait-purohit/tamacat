/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.tamacat.httpd.filter.RequestFilter;

/**
 * <p>The abstract class of authentication processor.
 */
public abstract class AbstractAuthProcessor implements RequestFilter {

	protected AuthComponent<?> authComponent;
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	
	@Override
	public void init() {
		if (authComponent != null) {
			authComponent.init();
		}
	}
	
	/**
	 * Set the {@link AuthComponent}. (required)
	 * @param authComponent
	 */
	public void setAuthComponent(AuthComponent<?> authComponent) {
		this.authComponent = authComponent;
	}

	/**
	 * Set the remote user key name. (optional)
	 * @param remoteUserKey
	 */
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}
}
