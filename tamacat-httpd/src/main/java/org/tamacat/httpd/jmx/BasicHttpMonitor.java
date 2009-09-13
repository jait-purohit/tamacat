/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

import javax.management.MXBean;

@MXBean
public interface BasicHttpMonitor {

	int getActiveConnections();
	
}
