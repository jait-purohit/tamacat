/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.internal.impl;

import org.tamacat.dao.rdb.RdbDaoEvent;

public class RdbDaoEventImpl implements RdbDaoEvent {

	private final Class<?> callerDao;
	private String query;
	private int result;
	
	public RdbDaoEventImpl(Class<?> callerDao){
		this.callerDao = callerDao;
	}
	
	public RdbDaoEventImpl(Class<?> callerDao, String query) {
		this.callerDao = callerDao;
		this.query = query;
	}
	
	public Class<?> getCallerDao() {
		return callerDao;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public int getResult() {
		return result;
	}
}
