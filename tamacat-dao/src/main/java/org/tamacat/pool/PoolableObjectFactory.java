/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.pool;

/**
 * Poolable Object Factory.
 *
 * @param <T> pooling target object.
 */
public interface PoolableObjectFactory<T> {

	/**
	 * Create the object.
	 * @return poolable object.
	 */
    T create();

    /**
     * It activates it.
     * @param object
     * @throws ObjectActivateException
     *     The ObjectActivateException is thrown out when failing in activating. 
     */
    void activate(T object);

    /**
     * Validating the object.
     * @param object
     * @return false is restored for an illegal object. true, otherwise.
     * @throws ObjectActivateException
     *   The ObjectActivateException is thrown out when failing in validation. 
     */
    boolean validate(T object);

    /**
     * Destroying the object.
     * @param object
     */
    void destroy(T object);
}
