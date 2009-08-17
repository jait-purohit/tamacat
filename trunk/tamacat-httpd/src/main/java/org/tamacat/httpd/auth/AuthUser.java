/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

/**
 * <p>It is the interface of the user object
 *  to use by the authentication.
 */
public interface AuthUser {
	
	String getAuthUsername();
	String getAuthPassword();
	
	/**
	 * Set the username to use by the authentication.
	 * @param password
	 */
	void setAuthUsername(String username);
	
	/**
	 * Set the password to use by the authentication.
	 * @param password
	 */
	void setAuthPassword(String password);
	
	/**
	 * When I use the encrypted password,
	 * I give back the {@code true}.
	 */
	boolean isEncrypted();
}
