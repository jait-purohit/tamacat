/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

public interface SessionListener {

	int getMaxInactiveInterval();
	
	void invalidate(Session session);
}
