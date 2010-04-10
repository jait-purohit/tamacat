/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tamacat.httpd.util.DefaultThreadFactory;

/**
 * <p>The factory class of {@link ExecutorService}.
 *  Support {@link Executors#newFixedThreadPool} or {@link Executors#newCachedThreadPool}.
 */
public class ThreadExecutorFactory {

	private final String threadName;
	
	/**
	 * <p>Constructs with the specified worker thread name.
	 * @param threadName
	 */
	public ThreadExecutorFactory(String threadName) {
		this.threadName = threadName;
	}
	
	/**
	 * <p>returns a fixed thread pool or cached thread pool.
	 * If fixed number of maximum threads, It use the fixed thread pool
	 * of {@code ExecutorService}.
	 * @return {@link ThreadPoolExecutor}
	 */
	public ExecutorService getExecutorService(int maxThreads) {
		if (maxThreads > 0) {
			return Executors.newFixedThreadPool(maxThreads, new DefaultThreadFactory(threadName));
		} else {
			return Executors.newCachedThreadPool(new DefaultThreadFactory(threadName));
		}
	}
}
