/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.tamacat.httpd.config.ServiceUrl;

public interface HttpFilter {
	
	void init(ServiceUrl serviceUrl);
}
