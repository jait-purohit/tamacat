/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.util;

public interface LimitedCacheObject {

	boolean isCacheExpired(long expire);
}
