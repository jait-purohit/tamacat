/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.pool.impl;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.pool.ObjectActivateException;
import org.tamacat.pool.ObjectPool;
import org.tamacat.pool.PoolableObjectFactory;

public class StackObjectPool<T> implements ObjectPool<T> {

	static final Log LOG = LogFactory.getLog(StackObjectPool.class);
	
    private final PoolableObjectFactory<T> factory;

    private final Stack<T> pool;
    private final AtomicInteger active = new AtomicInteger();
    private final AtomicInteger max = new AtomicInteger();

    public StackObjectPool(PoolableObjectFactory<T> factory) {
        this.factory = factory;
        pool = new Stack<>();
    }

    @Override
    public void free(T object) {
        if (factory.validate(object)) {
            pool.push(object);
        }
        active.decrementAndGet();
    }

    @Override
    public synchronized T getObject() {
    	T object = null;
        if (pool.size() > 0) {
            object = pool.pop();
            try {
              	LOG.trace("activate");
                factory.activate(object);
            } catch (ObjectActivateException e) {
            	LOG.warn("retry. " + e.getMessage());
                factory.destroy(object);
                object = getObject();
            } 
        } else {
        	object = create();
        }
        if (object != null) {
        	active.incrementAndGet();
        }
    	return object;
    }

    protected T create() {
        if (max.get() == 0 || active.get() < max.get()) {
            return factory.create();
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void setMaxPoolObject(int max) {
        this.max.set(max);
    }

    @Override
    public synchronized void release() {
        for (T o : pool) {
            factory.destroy(o);
        }
        pool.clear();
        active.set(0);
    }

    @Override
    public int getNumberOfMaxPoolObjects() {
        return max.get();
    }

    @Override
    public int getNumberOfPooledObjects() {
        return pool.size();
    }

    @Override
    public int getNumberOfActiveObjects() {
        return active.get();
    }
}
