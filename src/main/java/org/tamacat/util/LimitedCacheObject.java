/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

public interface LimitedCacheObject {

	boolean isCacheExpired(long expire);
}
