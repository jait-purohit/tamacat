/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter.acl;

import org.tamacat.httpd.util.LimitedCacheLRU;

public class AccessUrlCache extends LimitedCacheLRU<String, AccessUrl> {

	public AccessUrlCache(int maxSize, long expire) {
		super(maxSize, expire);
	}
}
