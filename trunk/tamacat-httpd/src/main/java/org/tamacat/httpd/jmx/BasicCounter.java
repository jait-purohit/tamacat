/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

public class BasicCounter {

	private int activeConnections;

	public int getActiveConnections() {
		return activeConnections;
	}

	public void countUp() {
		activeConnections++;
	}
	
	public void countDown() {
		activeConnections--;
	}
}
