/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import org.tamacat.util.CacheLRU;

public class GroovyClassCache extends CacheLRU<String, GroovyFile> {

	private long cacheExpireTime;

	/**
	 * <p>default cache size is 1000.
	 */
	GroovyClassCache() {
		super(1000);
	}
	
	GroovyClassCache(int max) {
		super(max);
	}
	
	public void setCacheExpireTime(long cacheExpireTime) {
		this.cacheExpireTime = cacheExpireTime;
	}
	
	@Override
	public synchronized GroovyFile get(String key) {
		GroovyFile file = super.get(key);
		if (file == null) {
			return null;
		} else if (System.currentTimeMillis() - file.getCreateTime() > cacheExpireTime) {
			//cache expired -> delete cache.
			super.remove(key);
			return null;
		} else {
			return file;
		}
	}
}
