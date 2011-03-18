/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

/**
 * Interface of Data access object.
 * @param <T> Data transfer object.
 */
public interface Dao<T> {

    int create(T data);
    int update(T data);
    int delete(T data);

    /**
     * The exception is appropriately processed, 
     * the exception object is converted, and it throws out. 
     * @param cause
     * @throws DaoException
     */
    void handleException(Throwable cause);
}
