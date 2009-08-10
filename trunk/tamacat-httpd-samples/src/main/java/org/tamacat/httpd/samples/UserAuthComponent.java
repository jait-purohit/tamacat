/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.samples;

import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;

public class UserAuthComponent implements AuthComponent<User> {

	private String password;
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean check(String id, String pass, HttpContext context) {
		return true;
	}

	@Override
	public User getAuthUser(String id, HttpContext context) {
		User user = new User();
		user.setAuthUsername(id);
		user.setAuthPassword(password);
		return user;
	}

	@Override
	public void init() {
	}
}
