package org.tamacat.httpd.jmx;

import javax.management.MXBean;

@MXBean
public interface PerformanceCounter {

	long getAverageResponseTime();
	
	long getMaximumResponseTime();
}
