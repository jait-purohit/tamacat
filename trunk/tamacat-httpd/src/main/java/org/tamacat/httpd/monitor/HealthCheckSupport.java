/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.monitor;

public interface HealthCheckSupport<T> {

	void removeTarget(T target);
	
	void addTarget(T target);
	
	void startHealthCheck();
}
