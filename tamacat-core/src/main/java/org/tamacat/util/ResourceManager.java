/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.util.Stack;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public final class ResourceManager {

	static final Log LOG = LogFactory.getLog(ResourceManager.class);
	
	private ThreadLocal<Stack<Object>> manager = new ThreadLocal<>();
	
	private final static ResourceManager self = new ResourceManager();
	
	public static ResourceManager getInstance() {
		return self;
	}
	
	private ResourceManager() {}
	
	private Stack<Object> getThreadObject() {
		Stack<Object> targets = manager.get();
		if (targets == null) {
			targets = new Stack<>();
			manager.set(targets);
		}
		return targets;
	}
	
	public void set(Object target) {
		if (target != null) {
			getThreadObject().add(target);
			LOG.trace("set: " + target);
		}
	}
	
	public void release() {
		Stack<Object> targets = getThreadObject();
		try {
			while (targets.size() > 0) {
				Object target = targets.pop();
				if (target != null) {
					LOG.trace("release: " + target.toString());
				}
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		LOG.trace("release.");
	}
}
