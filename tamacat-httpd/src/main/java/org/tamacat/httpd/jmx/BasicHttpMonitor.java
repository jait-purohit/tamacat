/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

import java.util.Date;

import javax.management.MXBean;

@MXBean
public interface BasicHttpMonitor {

	int getActiveConnections();
	
	Date getStartedTime();
	
	long getAccessCount();
	
	void resetAccessCount();
	
	long getErrorCount();
	
	void resetErrorCount();
	
}
