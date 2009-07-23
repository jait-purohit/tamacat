/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter.acl;

import org.tamacat.httpd.util.LimitedCacheObject;

public interface AccessUrl extends LimitedCacheObject {

	boolean isSuccess(String url);
	
}
