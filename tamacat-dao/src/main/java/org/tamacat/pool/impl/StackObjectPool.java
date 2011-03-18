/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.pool.impl;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.tamacat.pool.ObjectActivateException;
import org.tamacat.pool.ObjectPool;
import org.tamacat.pool.PoolableObjectFactory;

public class StackObjectPool<T> implements ObjectPool<T> {

    private final PoolableObjectFactory<T> factory;

    private final Stack<T> pool;
    private final AtomicInteger active = new AtomicInteger();
    private final AtomicInteger max = new AtomicInteger();

    public StackObjectPool(PoolableObjectFactory<T> factory) {
        this.factory = factory;
        pool = new Stack<T>();
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
        if (pool.size() > 0) {
            T object = pool.pop();
            if (object != null) {
                try {
                    factory.activate(object);
                    active.incrementAndGet();
                    return object;
                } catch (ObjectActivateException e) {
                    //retry
                    factory.destroy(object);
                    object = getObject();
                }
            }
        }
        T object = create();
        return object;
    }

    protected T create() {
        if (max.get() == 0 || active.get() < max.get()) {
            active.incrementAndGet();
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
