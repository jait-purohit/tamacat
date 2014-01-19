/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tamacat.httpd.core.DefaultWorkerExecutor;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The factory class of {@link ExecutorService}.
 *  Support {@link Executors#newFixedThreadPool} or {@link Executors#newCachedThreadPool}.
 * @since 1.1
 */
public class WebDavWorkerExecutor extends DefaultWorkerExecutor {
	static final Log LOG = LogFactory.getLog(WebDavWorkerExecutor.class);

	public WebDavWorkerExecutor() {
		super(new WebDavHttpRequestFactory());
	}
}
