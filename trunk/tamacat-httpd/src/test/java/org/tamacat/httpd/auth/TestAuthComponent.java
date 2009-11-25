package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;

public class TestAuthComponent implements AuthComponent<AuthUserImpl> {

	private String authPassword;
	
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}
	
	@Override
	public boolean check(String id, String pass, HttpContext context) {
		return pass != null && authPassword != null && pass.equals(authPassword);
	}

	@Override
	public AuthUserImpl getAuthUser(String id, HttpContext context) {
		AuthUserImpl user = new AuthUserImpl();
		user.setAuthUsername(id);
		user.setAuthPassword(authPassword);
		return user;
	}

	@Override
	public void init() {
	}
}
