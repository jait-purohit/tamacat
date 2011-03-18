/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Collection;

import org.tamacat.dao.Dao;
import org.tamacat.dao.DaoException;
import org.tamacat.dao.rdb.internal.RdbQuery;
import org.tamacat.dao.rdb.support.LogRdbDaoExecuterHandler;
import org.tamacat.dao.rdb.support.LogRdbDaoTransactionHandler;
import org.tamacat.sql.DBAccessManager;

public class RdbDaoAdapter<T extends ORMappingSupport> implements Dao<T> {
	
    protected RdbDao<T> delegate;    
    protected RdbDaoAdapter() {
    	setDatabase("default");
    }
    
    protected RdbDaoAdapter(RdbDao<T> delegate) {
    	setRdbDao(delegate);
    }

    public void setDatabase(String dbname) {
    	RdbDao<T> delegate = new RdbDao<T>();
    	delegate.callerDao = getClass();
    	delegate.setDatabase(dbname);
    	setRdbDao(delegate);
    }
    
    public void setRdbDao(RdbDao<T> delegate) {
    	this.delegate = delegate;
    	delegate.setExecuteHandler(new LogRdbDaoExecuterHandler());
        delegate.setTransactionHandler(new LogRdbDaoTransactionHandler());
    }

    protected void setDBAccessManager(DBAccessManager dbm) {
    	delegate.dbm = dbm;
    }
    
    public DBAccessManager getDBAccessManager() {
    	return delegate.getDBAccessManager();
    }
    
    public String param(RdbColumnMetaData column, Condition condition, String... values) {
        return delegate.param(column, condition, values);
    }

    public RdbQuery<T> createQuery() {
        return delegate.createQuery();
    }

    public RdbSearch createRdbSearch() {
        return delegate.createRdbSearch();
    }

    public RdbSort createRdbSort() {
        return delegate.createRdbSort();
    }

    public T search(RdbQuery<T> query) {
        return delegate.search(query);
    }

    public Collection<T> searchList(RdbQuery<T> query, int start, int max) {
        return delegate.searchList(query, start, max);
    }
    
    public Collection<T> searchList(RdbQuery<T> query) {
        return delegate.searchList(query);
    }
    
    public void handleException(Throwable cause) throws DaoException {
        delegate.handleException(cause);
    }

    protected String getInsertSQL(T data) {
        throw new RuntimeException(new NoSuchMethodException());
    }

    protected String getUpdateSQL(T data) {
        throw new RuntimeException(new NoSuchMethodException());
    }

    protected String getDeleteSQL(T data) {
        throw new RuntimeException(new NoSuchMethodException());
    }

    public int create(T data) {
        return delegate.executeUpdate(getInsertSQL(data));
    }

    public int update(T data) {
        return delegate.executeUpdate(getUpdateSQL(data));
    }

    public int delete(T data) {
        return delegate.executeUpdate(getDeleteSQL(data));
    }
    
    protected ResultSet executeQuery(String sql) throws DaoException {
    	return delegate.executeQuery(sql);
    }
    
    protected int executeUpdate(String sql) throws DaoException {
    	return delegate.executeUpdate(sql);
    }
    
    protected int executeUpdate(String sql, int index, InputStream in) throws DaoException {
    	return delegate.executeUpdate(sql, index, in);
    }
    
    protected void commit() {
    	delegate.commit();
    }

    protected void rollback() {
    	delegate.commit();
    }
    
    public void startTransaction() {
    	delegate.startTransaction();
    }
    
    public void endTransaction() {
    	delegate.endTransaction();
    }
    
    public void release() {
   		delegate.release();
    }
}
