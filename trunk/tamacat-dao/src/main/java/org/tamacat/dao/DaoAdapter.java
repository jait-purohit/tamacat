/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Collection;

import org.tamacat.dao.exception.DaoException;
import org.tamacat.dao.impl.LoggingDaoExecuterHandler;
import org.tamacat.dao.impl.LoggingDaoTransactionHandler;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.orm.ORMappingSupport;
import org.tamacat.sql.DBAccessManager;

public class DaoAdapter<T extends ORMappingSupport> implements AutoCloseable {
	
    protected Dao<T> delegate;    
    protected DaoAdapter() {
    	setDatabase("default");
    }
    
    protected DaoAdapter(Dao<T> delegate) {
    	setDao(delegate);
    }

    public void setDatabase(String dbname) {
    	Dao<T> delegate = new Dao<T>();
    	delegate.callerDao = getClass();
    	delegate.setDatabase(dbname);
    	setDao(delegate);
    }
    
    public void setDao(Dao<T> delegate) {
    	this.delegate = delegate;
    	delegate.setExecuteHandler(new LoggingDaoExecuterHandler());
        delegate.setTransactionHandler(new LoggingDaoTransactionHandler());
    }

    protected void setDBAccessManager(DBAccessManager dbm) {
    	delegate.dbm = dbm;
    }
    
    public DBAccessManager getDBAccessManager() {
    	return delegate.getDBAccessManager();
    }
    
    public String param(Column column, Condition condition, String... values) {
        return delegate.param(column, condition, values);
    }

    public Query<T> createQuery() {
        return delegate.createQuery();
    }

    public Search createSearch() {
        return delegate.createSearch();
    }

    public Sort createSort() {
        return delegate.createSort();
    }

    public T search(Query<T> query) {
        return delegate.search(query);
    }

    public Collection<T> searchList(Query<T> query, int start, int max) {
        return delegate.searchList(query, start, max);
    }
    
    public Collection<T> searchList(Query<T> query) {
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
    
    public void commit() {
    	delegate.commit();
    }

    public void rollback() {
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

	@Override
	public void close() throws Exception {
		release();
	}
}
