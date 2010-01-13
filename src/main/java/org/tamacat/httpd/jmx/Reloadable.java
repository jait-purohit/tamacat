package org.tamacat.httpd.jmx;

import javax.management.MXBean;

/**
 * <p>Support the reload operation of JMX.
 */
@MXBean
public interface Reloadable {

	/**
	 * <p>Reload the configuration.
	 */
	void reload();
}
