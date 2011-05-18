/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;

/**
 * <p>The interface of Authentication component.
 */
public interface AuthComponent<T extends AuthUser> {

	/**
	 * <p>Remote user key used for HttpContext.
	 */
	String REMOTE_USER_KEY = AuthComponent.class.getClass().getName() + ".REMOTE_USER";

	/**
	 * <p>Initialized this instance.
	 * Execute from {@link HttpHandler#setRequestFilter}.
	 */
	void init();
	
	/**
	 * <p>Release all object used by this instance.
	 */
	void release();
	
	/**
	 * <p>The login account is verified. 
	 * @param id
	 * @param pass
	 * @param context
	 * @throws {@link org.tamacat.server.http.UnauthorizedException}
	 */
	boolean check(String id, String pass, HttpContext context);
	
	/**
	 * <p>Get the {@link AuthUser}.
	 * @param id
	 * @param context
	 * @return Implements of {@code AuthUser}
	 */
	T getAuthUser(String id, HttpContext context);
}
