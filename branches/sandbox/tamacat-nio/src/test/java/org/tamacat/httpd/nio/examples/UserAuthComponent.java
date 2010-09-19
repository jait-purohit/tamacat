/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio.examples;

import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;

public class UserAuthComponent implements AuthComponent<User> {

	private String username;
	private String password;
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean check(String id, String pass, HttpContext context) {
		return pass != null && password != null 
	        && id != null && username != null
	        && pass.equals(password)
	        && id.equals(username);
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
