/*
 * Copyright (c) 2013, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.util.DefaultThreadFactory;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The factory class of {@link ExecutorService}.
 *  Support {@link Executors#newFixedThreadPool} or {@link Executors#newCachedThreadPool}.
 * @since 1.1
 */
public class DefaultExecutorFactory implements ExecutorFactory {
	static final Log LOG = LogFactory.getLog(DefaultExecutorFactory.class);

	protected ServerConfig serverConfig;
	protected String name;
	protected int maxThreads;
	protected ExecutorService executorService;
	protected DefaultThreadFactory factory = new DefaultThreadFactory();

	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		//set the maximun worker threads.
		maxThreads = serverConfig.getMaxThreads();
		LOG.info("MaxServerThreads: " + maxThreads);
		name = serverConfig.getParam("WorkerThreadName", "httpd");
		factory.setName(name);
	}

	/**
	 * <p>returns a fixed thread pool or cached thread pool.
	 * If fixed number of maximum threads, It use the fixed thread pool
	 * of {@code ExecutorService}.
	 * @return {@link ThreadPoolExecutor}
	 */
	@Override
	public ExecutorService getExecutorService() {
		if (executorService == null) {
			if (maxThreads > 0) {
				executorService = Executors.newFixedThreadPool(maxThreads, factory);
			} else {
				executorService = Executors.newCachedThreadPool(factory);
			}
		}
		return executorService;
	}

	@Override
	public void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
