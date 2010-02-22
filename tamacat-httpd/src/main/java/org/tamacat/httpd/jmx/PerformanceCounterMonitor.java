package org.tamacat.httpd.jmx;

import javax.management.MXBean;

@MXBean
public interface PerformanceCounterMonitor extends BasicHttpMonitor,
		PerformanceCounter {

	String getPath();
}
