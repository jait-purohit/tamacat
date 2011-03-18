/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.pool.impl;

import org.tamacat.pool.ObjectActivateException;
import org.tamacat.pool.PoolableObjectFactory;

public class ConnectionFactory implements PoolableObjectFactory<Connection> {

    public void activate(Connection object) {
        if (object == null) throw new ObjectActivateException();
    }

    public Connection create() {
        return new Connection();
    }

    public boolean validate(Connection object) {
        return object != null ? true : false;
    }

    public void destroy(Connection object) {
    }
}
