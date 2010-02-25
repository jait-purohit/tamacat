package org.tamacat.httpd.jmx;

import javax.management.MXBean;

/**
 * <p>Support the monitor, reload and restart operation of JMX for Httpd.
 */
@MXBean
public interface JMXReloadableHttpd	extends Reloadable {

	/**
	 * <p>start the Httpd.
	 */
	void startHttpd();
	
	/**
	 * <p>stop the Httpd.
	 */
	void stopHttpd();
	
	void restartHttpd();
	
	int getMaxServerThreads();
	
	void setMaxServerThreads(int max);
}
