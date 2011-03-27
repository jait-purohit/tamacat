/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.tamacat.dao.DaoException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.sql.LifecycleSupport;
import org.tamacat.sql.ResourceManager;

public final class DBAccessManager implements LifecycleSupport {

	static final Log LOG = LogFactory.getLog(DBAccessManager.class);
	
    private static final HashMap<String, DBAccessManager> MANAGER
        = new HashMap<String, DBAccessManager>();

    public static synchronized DBAccessManager getInstance(String name) {
        DBAccessManager dm = MANAGER.get(name);
        if (dm == null) {
            dm = new DBAccessManager(name);
        }
        return dm;
    }
    
    private ThreadLocal<Boolean> running = new ThreadLocal<Boolean>();
    private ThreadLocal<Connection> con = new ThreadLocal<Connection>();
    private ThreadLocal<Statement> stmt = new ThreadLocal<Statement>();
    private String name;

    private DBAccessManager(String name) {
        this.name = name;
        if (running.get() == null) { //initialize running flag.
        	running.set(false);
        }
    }

    Connection getConnection() {
        Connection c = con.get();
        try {
	        if (c == null || c.isClosed()) {
	            c = ConnectionManager.getInstance(name).getObject();
	            if (c != null) {
	            	con.set(c);
	            	start();
	            }
	        }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return c;
    }

    Statement getStatement() {
        Statement s = stmt.get();
        try {
        	if (s == null || s.isClosed()) {
                s = getConnection().createStatement();
                if (s != null) {
                    stmt.set(s);
                }
	        }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return s;
    }

    public Statement createStatement() {
        try {
            return getConnection().createStatement();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public PreparedStatement preparedStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
    
    public ResultSet executeQuery(String sql) {
        try {
            return getStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public int executeUpdate(String sql) {
        try {
            return getStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void close(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
    }

    public void close(Statement st) {
        try {
            if (st != null) st.close();
        } catch (SQLException e) {
        	LOG.warn(e.getMessage());
        }
    }

    public void setAutoCommit(boolean autoCommit) {
    	try {
    		if (getAutoCommit()) {
    			getConnection().setAutoCommit(autoCommit);
    		}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
    }
    
    public boolean getAutoCommit() {
    	try {
			return getConnection().getAutoCommit();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
    }
    
    public void commit() {
    	try {
			getConnection().commit();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
    }
    
    public void rollback() {
    	try {
			getConnection().rollback();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
    }
    
    public void release() {
    	if (isRunning()) {
    		Statement st = stmt.get();
        	close(st);
        	Connection c = con.get();
        	ConnectionManager.getInstance(name).free(c);
        	LOG.debug("released.");
    	}
    }

    @Override
	public boolean isRunning() {
		return running.get();
	}

    @Override
	public void start() {
		ResourceManager.set(this);
		running.set(true);
	}
    
    @Override
	public void stop() {
		release();
		running.set(false);
	}
}
