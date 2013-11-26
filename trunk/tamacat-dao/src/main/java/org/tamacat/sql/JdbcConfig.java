/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.sql;

import java.sql.Connection;

import org.tamacat.pool.ObjectActivateException;

public interface JdbcConfig {

    Connection getConnection();
    
    String getUrl();
    
    public String getDriverClass();
    
    void activate(Connection con) throws ObjectActivateException;
}
