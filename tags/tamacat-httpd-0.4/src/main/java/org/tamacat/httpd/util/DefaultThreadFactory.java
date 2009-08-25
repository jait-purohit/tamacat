/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.concurrent.ThreadFactory;

/**
 * <p>Implements the default {@link ThreadFactory}.<br>
 * {@code Thread name: name-$count}
 */
public class DefaultThreadFactory implements ThreadFactory {

	private static volatile int COUNT = 0;
    private final String name;
    
	public DefaultThreadFactory(String name) {
		this.name = name;
	}
	
    @Override
    public Thread newThread(final Runnable r) {
        return new Thread(r, name + "-" + (++COUNT));
    }
}
