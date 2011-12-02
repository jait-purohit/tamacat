/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.tamacat.dao.event.DaoEvent;
import org.tamacat.dao.event.DaoExecuteHandler;
import org.tamacat.dao.event.DaoTransactionHandler;
import org.tamacat.dao.exception.DaoException;
import org.tamacat.dao.impl.NoneDaoExecuteHandler;
import org.tamacat.dao.impl.NoneDaoTransactionHandler;
import org.tamacat.dao.impl.DaoEventImpl;
import org.tamacat.dao.impl.QueryImpl;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.orm.ORMapper;
import org.tamacat.dao.orm.ORMappingSupport;
import org.tamacat.dao.util.BlobUtils;
import org.tamacat.sql.DBAccessManager;
import org.tamacat.sql.DBUtils;
import org.tamacat.sql.IllegalTransactionStateException;
import org.tamacat.sql.SQLParser;
import org.tamacat.sql.TransactionStateManager;
import org.tamacat.util.ClassUtils;

public class Dao<T extends ORMappingSupport> {

	protected static final String DEFAULT_DBNAME = "default";
	
	protected static final DaoTransactionHandler DEFAULT_TRANSACTION_HANDLER
		= new NoneDaoTransactionHandler();
	
	protected static final DaoExecuteHandler DEFAULT_EXECUTE_HANDLER
		= new NoneDaoExecuteHandler();
	
	protected Class<?> callerDao;// = getClass();
	
	protected DBAccessManager dbm;
	protected ORMapper<T> orm;
	protected SQLParser parser = new SQLParser();
    
    private DaoExecuteHandler executeHandler;
    private DaoTransactionHandler transactionHandler;
    private DaoEvent event;
    
    protected DaoExecuteHandler getExecuteHandler() {
    	if (executeHandler == null) executeHandler = DEFAULT_EXECUTE_HANDLER;
		return executeHandler;
	}

	public void setExecuteHandler(DaoExecuteHandler executeHandler) {
		this.executeHandler = executeHandler;
	}

	protected DaoTransactionHandler getTransactionHandler() {
		if (transactionHandler == null) transactionHandler = DEFAULT_TRANSACTION_HANDLER; 
		return transactionHandler;
	}

	public void setTransactionHandler(DaoTransactionHandler transactionHandler) {
		this.transactionHandler = transactionHandler;
	}

    public Dao() {
        orm = new ORMapper<T>();
    }
    
    public Dao(DBAccessManager dbm) {
    	this.dbm = dbm;
    	orm = new ORMapper<T>();
    }
    
    @SuppressWarnings("unchecked")
	public void setDatabase(String dbname) {
    	dbm = DBAccessManager.getInstance(dbname);
        Type[] types = ClassUtils.getParameterizedTypes(getCallerDaoClass());
        if (types.length > 0) {
        	orm.setPrototype((Class<T>)types[0]);
        }
    }
    
    public void setPrototype(Class<T> prototype) {
        orm.setPrototype(prototype);
    }
    
    public void setPrototype(String name) {
        orm.setPrototype(name);
    }

    public DBAccessManager getDBAccessManager() {
    	if (dbm == null) {
    		dbm = DBAccessManager.getInstance(DEFAULT_DBNAME);
    	}
    	return dbm;
    }
    
    public String param(Column column, Condition condition, String... values) {
        return parser.value(column, condition, values);
    }

    public Search createRdbSearch() {
        return new Search();
    }

    public Sort createRdbSort() {
        return new Sort();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Query<T> createQuery() {
        return new QueryImpl();
    }

    public T search(Query<T> query) {
        ResultSet rs = executeQuery(query.getSelectSQL());
        T o = null;
        try {
            if (rs.next()) {
                o = mapping(query.getSelectColumns(), rs).getMappedObject();
            } else {
                o = orm.getMappedObject();
            }
        } catch (SQLException e) {
            handleException(e);
        } finally {
        	DBUtils.close(rs);
        }
        return o;
    }

    protected ORMapper<T> mapping(Collection<Column> columns, ResultSet rs) {
        return orm.mapping(columns, rs) ;
    }
    
    public Collection<T> searchList(Query<T> query) {
    	return searchList(query, -1, -1);
    }
    
    public Collection<T> searchList(Query<T> query, int start, int max) {
        Collection<Column>columns = query.getSelectColumns();
        ResultSet rs = executeQuery(query.getSelectSQL());
        ArrayList<T> list = new ArrayList<T>();
        try {
        	if (start > 0) {
        		for (int i=1; i<start; i++) rs.next();
        	}
            int add = 0;
            while (rs.next()) {
                T o = mapping(columns, rs).getMappedObject();
                list.add(o);
                add ++;
                if (max > 0 && add >= max) break;
            }
        } catch (SQLException e) {
            handleException(e);
        } finally {
        	DBUtils.close(rs);
        }
        return list;
    }

    /**
     * The exception is appropriately processed, 
     * the exception object is converted, and it throws out. 
     * @param cause
     * @throws DaoException
     */
    public void handleException(Throwable cause) {
    	DaoEvent event = createRdbDaoEvent();
   		getTransactionHandler().handleException(event, cause);
        throw new DaoException(cause);
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
        return executeUpdate(getInsertSQL(data));
    }

    public int update(T data) {
        return executeUpdate(getUpdateSQL(data));
    }

    public int delete(T data) {
        return executeUpdate(getDeleteSQL(data));
    }
    
    protected Class<?> getCallerDaoClass() {
    	return callerDao != null? callerDao : getClass();
    }
    
    DaoEvent createRdbDaoEvent(String sql) {
    	return new DaoEventImpl(getCallerDaoClass(), sql);
    }
    
    DaoEvent createRdbDaoEvent() {
    	return new DaoEventImpl(getCallerDaoClass());
    }

    protected ResultSet executeQuery(String sql) throws DaoException {
    	DaoEvent event = createRdbDaoEvent(sql);
       	getExecuteHandler().handleBeforeExecuteQuery(event);
        ResultSet rs = dbm.executeQuery(sql);
        getExecuteHandler().handleAfterExecuteQuery(event);
        return rs;
    }

    protected int executeUpdate(String sql) throws DaoException {
    	DaoEvent event = createRdbDaoEvent(sql);
    	getExecuteHandler().handleBeforeExecuteUpdate(event);
        int result = dbm.executeUpdate(sql);
        TransactionStateManager.getInstance().executed();
       	event.setResult(result);
       	return getExecuteHandler().handleAfterExecuteUpdate(event);
    }
    
    protected int executeUpdate(String sql, int index, InputStream in) throws DaoException {
    	DaoEvent event = createRdbDaoEvent(sql);
    	getExecuteHandler().handleBeforeExecuteUpdate(event);
    	PreparedStatement stmt = dbm.preparedStatement(sql);
   		int result = BlobUtils.executeUpdate(stmt, index, in);
        TransactionStateManager.getInstance().executed();
       	event.setResult(result);
       	return getExecuteHandler().handleAfterExecuteUpdate(event);
    }
    
    protected void commit() throws DaoException {
    	DaoEvent event = createRdbDaoEvent();
    	getTransactionHandler().handleBeforeCommit(event);
    	dbm.commit();
    	TransactionStateManager.getInstance().commit();
   		getTransactionHandler().handleAfterCommit(event);
    }
    
    protected void rollback() throws DaoException {
    	DaoEvent event = createRdbDaoEvent();
    	getTransactionHandler().handleBeforeRollback(event);
    	dbm.rollback();
   		TransactionStateManager.getInstance().rollback();
   		getTransactionHandler().handleAfterRollback(event);
    }
    
    protected boolean isTransactionStarted() {
    	return TransactionStateManager.getInstance().isTransactionStarted();
    }
    
    protected void startTransaction() throws DaoException {
    	DBUtils.setAutoCommitFalse(dbm);
    	if (isTransactionStarted() == false) {
    		TransactionStateManager.getInstance().begin();
    		event = createRdbDaoEvent();
    		getTransactionHandler().handleTransantionStart(event);
    	}
    }
    
    protected void endTransaction() throws DaoException {
    	if (isTransactionStarted() == false) {
    		throw new IllegalTransactionStateException("Transaction is not started.");
    	}
    	if (TransactionStateManager.getInstance().isNotCommited()) {
    		rollback();
    		abortTransaction();
    	}
    	DBUtils.setAutoCommitTrue(dbm);
    	TransactionStateManager.getInstance().end();
    	DaoEvent event = createRdbDaoEvent();
		getTransactionHandler().handleTransantionEnd(event);
    }
    
    void abortTransaction() {
    	throw new IllegalTransactionStateException("Transaction is not commit or rollback. Force execute rollback.");
    }
    
    protected void release() {
    	dbm.release();
    	DaoEvent event = createRdbDaoEvent();
    	getTransactionHandler().handleRelease(event);
    }
}
