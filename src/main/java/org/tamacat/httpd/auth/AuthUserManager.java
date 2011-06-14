package org.tamacat.httpd.auth;

public class AuthUserManager {

	static ThreadLocal<AuthUser> manager = new ThreadLocal<AuthUser>();
	
	public AuthUser get() {
		return manager.get();
	}
	
	public void set(AuthUser user) {
		manager.set(user);
	}
	
	public void remove() {
		manager.remove();
	}
}
