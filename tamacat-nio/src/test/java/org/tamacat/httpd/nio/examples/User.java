/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio.examples;

import org.tamacat.httpd.auth.AuthUser;

public class User implements AuthUser {

	private String username;
	private String password;
	
	@Override
	public String getAuthPassword() {
		return password;
	}

	@Override
	public String getAuthUsername() {
		return username;
	}

	@Override
	public boolean isEncrypted() {
		return false;
	}

	@Override
	public void setAuthPassword(String password) {
		this.password = password;
	}

	@Override
	public void setAuthUsername(String username) {
		this.username = username;
	}
}
