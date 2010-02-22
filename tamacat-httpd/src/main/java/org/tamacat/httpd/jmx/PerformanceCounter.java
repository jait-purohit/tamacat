package org.tamacat.httpd.jmx;

import javax.management.MXBean;

@MXBean
public interface PerformanceCounter {

	void countUp();
	
	void countDown();
	
	long getAverageResponseTime();
	
	long getMaximumResponseTime();
}
