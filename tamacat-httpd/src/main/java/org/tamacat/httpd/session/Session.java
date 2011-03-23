/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * <p>{@code Session} interface like HttpSession of Servlet-API.
 */
public interface Session extends Serializable {

	String getId();
	
	Object getAttribute(String key);
	
	void setAttribute(String key, Object value);
	
	void removeAttribute(String key);
	
	Set<String> getAttributeKeys();
	
	Date getCreationDate();
	
	Date getLastAccessDate();
	
	void setLastAccessDate(Date lastAccessDate);
	
	int getMaxInactiveInterval();
	
	void setMaxInactiveInterval(int maxInactiveInterval);
	
	void invalidate();
}
