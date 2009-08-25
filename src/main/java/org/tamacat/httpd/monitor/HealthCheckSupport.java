/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.monitor;

/**
 * <p>It is interface to express that I support a health check function.
 * 
 * @param <T> target of health check object.
 */
public interface HealthCheckSupport<T> {

	/**
	 * <p>Start the {@link HttpMonitor} thread.
	 */
	void startHealthCheck();
	
	/**
	 * <p>When there was a problem by a health check, it is carried out.
	 * @param target remove the health check target
	 */
	void removeTarget(T target);
	
	/**
	 * <p>When I restored by a health check normally, it is carried out.
	 * @param target add the health check target
	 */
	void addTarget(T target);
}
