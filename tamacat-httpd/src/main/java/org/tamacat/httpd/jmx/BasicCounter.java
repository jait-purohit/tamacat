/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.jmx;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BasicCounter implements BasicHttpMonitor {

	private AtomicInteger activeConnections;
	private AtomicLong accessCount;
	private AtomicLong errorCount;

	private final Date startedTime = new Date();
	
	public int getActiveConnections() {
		return activeConnections.get();
	}

	public void countUp() {
		activeConnections.incrementAndGet();
	}
	
	public void countDown() {
		activeConnections.decrementAndGet();
	}

	@Override
	public long getAccessCount() {
		return accessCount.get();
	}
	
	@Override
	public void resetAccessCount() {
		accessCount.set(0);
	}
	
	public void access() {
		accessCount.incrementAndGet();
	}

	@Override
	public long getErrorCount() {
		return errorCount.get();
	}
	
	@Override
	public void resetErrorCount() {
		errorCount.set(0);
	}
	
	public void error() {
		errorCount.incrementAndGet();
	}

	@Override
	public Date getStartedTime() {
		return startedTime;
	}
}
