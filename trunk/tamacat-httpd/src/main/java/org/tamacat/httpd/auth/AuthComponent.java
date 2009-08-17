/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;

/**
 * <p>The interface of Authentication component.
 * (Thread unsafe)
 */
public interface AuthComponent<T extends AuthUser> {

	String REMOTE_USER_KEY = AuthComponent.class.getClass().getName() + ".REMOTE_USER";

	/**
	 * <p>Execute from {@link HttpHandler#setRequestFilter}.
	 */
	void init();
	
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
	 * @return
	 */
	T getAuthUser(String id, HttpContext context);
}
