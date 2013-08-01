/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.lb;



/**
 * <p>It is interface to express that I support a health check function.
 * 
 * @param <T> target of health check object.
 */
public interface HealthCheckSupport<T> extends MonitorEvent<T> {

	/**
	 * <p>Start the {@link HttpMonitor} thread.
	 */
	void startHealthCheck();
}
