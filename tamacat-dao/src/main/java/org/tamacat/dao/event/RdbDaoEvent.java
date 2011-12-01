/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.event;

public interface RdbDaoEvent {
	
	Class<?> getCallerDao();
	
	void setQuery(String query);
	
	String getQuery();
	
	void setResult(int result);
	int getResult();
}
