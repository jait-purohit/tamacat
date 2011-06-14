package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;

/**
 * <p>{@code AbstractAuthComponent} is common implementation for {@code AuthComponent}.
 * AuthUser can be acquired from AuthUserManager when attested.
 * @param <T> extends AuthUser
 */
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
