package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;

public abstract class AbstractAuthComponent<T extends AuthUser> implements AuthComponent<T> {

	protected AuthUserManager manager = new AuthUserManager();
	
	@Override
	public void init() {
	}
	
	@Override
	public void release() {
		manager.remove();
	}
	
	@Override
	public boolean check(String id, String pass, HttpContext context) {
		if (id != null && pass != null) {
			T user = getAuthUser(id, context);
			if (user != null) {
				if (id.equals(user.getAuthUsername())
				 && pass.equals(user.getAuthPassword())) {
					manager.set(user);
					return true;
				}
			}
		}
		return false;
	}
}
