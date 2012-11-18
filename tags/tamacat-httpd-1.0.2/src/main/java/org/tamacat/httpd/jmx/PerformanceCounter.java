/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

import javax.management.MXBean;

@MXBean
public interface PerformanceCounter {

	int getActiveConnections();

	int countUp();
	
	int countDown();
	
	void reset();
	
	long getAverageResponseTime();
	
	long getMaximumResponseTime();
}
