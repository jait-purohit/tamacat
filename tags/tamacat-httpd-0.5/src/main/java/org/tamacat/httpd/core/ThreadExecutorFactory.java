/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.util.DefaultThreadFactory;

/**
 * <p>The factory class of {@link ExecutorService}.
 *  Support {@link Executors#newFixedThreadPool} or {@link Executors#newCachedThreadPool}.
 */
public class ThreadExecutorFactory {

	private final ServerConfig serverConfig;
	
	/**
	 * <p>Constructs with the specified {@link ServerConfig}.
	 * @param serverConfig
	 */
	public ThreadExecutorFactory(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	/**
	 * <p>returns a fixed thread pool or cached thread pool.
	 * If fixed number of maximum threads, It use the fixed thread pool
	 * of {@code ExecutorService}.
	 * @return {@link ThreadPoolExecutor}
	 */
	public ExecutorService getExecutorService() {
		int maxThreads = serverConfig.getMaxThreads();
		if (maxThreads > 0) {
			return Executors.newFixedThreadPool(maxThreads, new DefaultThreadFactory("Proxy"));
		} else {
			return Executors.newCachedThreadPool(new DefaultThreadFactory("Proxy"));
		}
	}
}
