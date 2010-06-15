/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import java.util.Date;

public class GroovyFile {
	private Class<?> type;
	private long updateTime;
	private final long createTime;
	
	GroovyFile(Class<?> type, long updateTime) {
		this.type = type;
		this.updateTime = updateTime;
		this.createTime = new Date().getTime();
	}
	
	public Class<?> loadClass() {
		return type;
	}
	
	public boolean isUpdate(long time) {
		return updateTime < time;
	}
	
	public long getCreateTime() {
		return createTime;
	}
}
