/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.tamacat.di.DI;
import org.tamacat.pool.ObjectActivateException;
import org.tamacat.pool.PoolableObjectFactory;
import org.tamacat.pool.impl.StackObjectPool;
import org.tamacat.util.ResourceNotFoundException;

public class ConnectionManager extends StackObjectPool<Connection> {

	private static final String XML = "db.xml";
	
    private static final HashMap<String, ConnectionManager>
        MANAGER = new HashMap<String, ConnectionManager>();

    public synchronized static ConnectionManager getInstance(String name) {
        ConnectionManager cm = MANAGER.get(name);
        if (cm == null) {
            cm = new ConnectionManager(name);
            MANAGER.put(name, cm);
        }
        return cm;
    }

    private ConnectionManager(String name) {
        super(new ConnectionFactory(name));
    }

    static class ConnectionFactory implements PoolableObjectFactory<Connection> {
        private JdbcConfig config;

        public ConnectionFactory(String name) {
            config = DI.configure(XML).getBean(name, JdbcConfig.class);
            if (config == null) {
            	throw new ResourceNotFoundException(XML + " of key [" + name + "] is not found.");
            }
        }

        @Override
        public void activate(Connection object) throws ObjectActivateException {
            config.activate(object);
        }

        @Override
        public Connection create() {
            return config.getConnection();
        }

        @Override
        public void destroy(Connection object) {
            if (object != null) {
                try {
                    object.close();
                } catch (SQLException e) {
                }
            }
        }

        @Override
        public boolean validate(Connection object)
                throws ObjectActivateException {
            return object != null ? true : false;
        }
    }
}
