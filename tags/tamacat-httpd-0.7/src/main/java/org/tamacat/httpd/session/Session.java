/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.util.Date;
import java.util.Set;

/**
 * <p>{@code Session} interface like HttpSession of Servlet-API.
 */
public interface Session {

	String getId();
	
	Object getAttribute(String key);
	
	void setAttribute(String key, Object value);
	
	void removeAttribute(String key);
	
	Set<String> getAttributeKeys();
	
	Date getCreationDate();
	
	int getMaxInactiveInterval();
	
	void setMaxInactiveInterval(int maxInactiveInterval);
	
	void invalidate();
}