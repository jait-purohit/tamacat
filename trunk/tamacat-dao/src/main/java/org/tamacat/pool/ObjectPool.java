/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.pool;

public interface ObjectPool<T> {

    T getObject();

    void free(T object);

    void release();

    void setMaxPoolObject(int max);

    int getNumberOfMaxPoolObjects();

    int getNumberOfPooledObjects();

    int getNumberOfActiveObjects();
}
