/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

public interface AuthUser {
	String getAuthUsername();
	String getAuthPassword();
	
	void setAuthUsername(String username);
	void setAuthPassword(String password);
	
	boolean isEncrypted();
}
