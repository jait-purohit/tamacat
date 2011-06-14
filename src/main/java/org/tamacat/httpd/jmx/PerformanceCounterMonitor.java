/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

import javax.management.MXBean;

@MXBean
public interface PerformanceCounterMonitor extends BasicHttpMonitor,
		PerformanceCounter {

	String getPath();
}
