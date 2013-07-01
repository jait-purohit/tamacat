package org.tamacat.httpd.auth;

import org.tamacat.util.LimitedCacheLRU;

public class AuthUserCache extends LimitedCacheLRU<String, CacheSupportAuthUser> {

	public AuthUserCache(int maxSize, long expire) {
		super(maxSize, expire);
	}
}
