/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

/**
 * <p>The type of service URL.
 */
public enum ServiceType {
	
	/**
	 * The service url type of normal http server.
	 */
	NORMAL,
	
	/**
	 * The service url type of reverse proxy.
	 */
	REVERSE,
	
	/**
	 * The service url type of reverse proxy with load balancing.
	 */
	LB,
	
	/**
	 * The service url type of error page.
	 */
	ERROR;
	
	public static ServiceType find(String name) {
		return valueOf(name.toUpperCase());
	}
}
