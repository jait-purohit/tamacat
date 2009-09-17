/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

import java.util.Date;

public class BasicCounter implements BasicHttpMonitor {

	private int activeConnections;
	private long accessCount;
	private long errorCount;

	private final Date startedTime = new Date();
	
	public int getActiveConnections() {
		return activeConnections;
	}

	public void countUp() {
		activeConnections++;
	}
	
	public void countDown() {
		activeConnections--;
	}

	@Override
	public long getAccessCount() {
		return accessCount;
	}
	
	@Override
	public void resetAccessCount() {
		accessCount = 0;
	}
	
	public void access() {
		accessCount++;
	}

	@Override
	public long getErrorCount() {
		return errorCount;
	}
	
	@Override
	public void resetErrorCount() {
		errorCount = 0;
	}
	
	public void error() {
		errorCount ++;
	}

	@Override
	public Date getStartedTime() {
		return startedTime;
	}
}
