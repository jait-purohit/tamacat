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
	
	/**
	 * <p>Get the username
	 * @return username
	 */
	String getAuthUsername();
	
	/**
	 * <p>Get the password
	 * @return password
	 */
	String getAuthPassword();
	
	/**
	 * <p>Set the username to use by the authentication.
	 * @param password
	 */
	void setAuthUsername(String username);
	
	/**
	 * <p>Set the password to use by the authentication.
	 * @param password
	 */
	void setAuthPassword(String password);
	
	/**
	 * <p>When I use the encrypted password,
	 * I give back the {@code true}.
	 */
	boolean isEncrypted();
}
