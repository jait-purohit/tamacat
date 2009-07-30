/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.util.DefaultThreadFactory;

public class ThreadExecutorFactory {

	private final ServerConfig serverConfig;
	
	public ThreadExecutorFactory(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	public ExecutorService getExecutorService() {
		int maxThreads = serverConfig.getMaxThreads();
		if (maxThreads > 0) {
			return Executors.newFixedThreadPool(maxThreads, new DefaultThreadFactory("Proxy"));
		} else {
			return Executors.newCachedThreadPool(new DefaultThreadFactory("Proxy"));
		}
	}
}
