/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.mock.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

//import org.easymock.EasyMock;
//import org.easymock.IMocksControl;

public class MockDriver implements Driver {

	private Connection connection;
	
    static {
        try {
            DriverManager.registerDriver(new MockDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public MockDriver() {
        this.connection = new MockConnection();
    }

    public void setConnection(Connection connection) {
    	this.connection = connection;
    }
    
    public boolean acceptsURL(String url) throws SQLException {
        return true;
    }

    public Connection connect(String url, Properties info) throws SQLException {
        //IMocksControl control = EasyMock.createControl();
        //return control.createMock(Connection.class);
        return connection;
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
            throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public boolean jdbcCompliant() {
        return true;
    }
}