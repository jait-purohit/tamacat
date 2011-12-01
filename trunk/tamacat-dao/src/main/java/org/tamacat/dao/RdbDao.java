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

import org.tamacat.dao.event.RdbDaoEvent;
import org.tamacat.dao.event.RdbDaoExecuteHandler;
import org.tamacat.dao.event.RdbDaoTransactionHandler;
import org.tamacat.dao.exception.DaoException;
import org.tamacat.dao.impl.NoneRdbDaoExecuteHandler;
import org.tamacat.dao.impl.NoneRdbDaoTransactionHandler;
import org.tamacat.dao.impl.RdbDaoEventImpl;
import org.tamacat.dao.impl.RdbQueryImpl;
import org.tamacat.dao.meta.RdbColumnMetaData;
import org.tamacat.dao.orm.ORMapper;
import org.tamacat.dao.orm.ORMappingSupport;
import org.tamacat.dao.util.BlobUtils;
import org.tamacat.sql.DBAccessManager;
import org.tamacat.sql.DBUtils;
import org.tamacat.sql.IllegalTransactionStateException;
import org.tamacat.sql.TransactionStateManager;
import org.tamacat.util.ClassUtils;

public class RdbDao<T extends ORMappingSupport> {

	protected static final String DEFAULT_DBNAME = "default";
	
	protected static final RdbDaoTransactionHandler DEFAULT_TRANSACTION_HANDLER
		= new NoneRdbDaoTransactionHandler();
	
	protected static final RdbDaoExecuteHandler DEFAULT_EXECUTE_HANDLER
		= new NoneRdbDaoExecuteHandler();
	
	protected Class<?> callerDao;// = getClass();
	
	protected DBAccessManager dbm;
	protected ORMapper<T> orm;
	protected SQLParser parser = new SQLParser();
    
    private RdbDaoExecuteHandler executeHandler;
    private RdbDaoTransactionHandler transactionHandler;
    private RdbDaoEvent event;
    
    protected RdbDaoExecuteHandler getExecuteHandler() {
    	if (executeHandler == null) executeHandler = DEFAULT_EXECUTE_HANDLER;
		return executeHandler;
	}

	public void setExecuteHandler(RdbDaoExecuteHandler executeHandler) {
		this.executeHandler = executeHandler;
	}

	protected RdbDaoTransactionHandler getTransactionHandler() {
		if (transactionHandler == null) transactionHandler = DEFAULT_TRANSACTION_HANDLER; 
		return transactionHandler;
	}

	public void setTransactionHandler(RdbDaoTransactionHandler transactionHandler) {
		this.transactionHandler = transactionHandler;
	}

    public RdbDao() {
        orm = new ORMapper<T>();
    }
    
    public RdbDao(DBAccessManager dbm) {
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
    
    public String param(RdbColumnMetaData column, Condition condition, String... values) {
        return parser.value(column, condition, values);
    }

    public RdbSearch createRdbSearch() {
        return new RdbSearch();
    }

    public RdbSort createRdbSort() {
        return new RdbSort();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RdbQuery<T> createQuery() {
        return new RdbQueryImpl();
    }

    public T search(RdbQuery<T> query) {
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

    protected ORMapper<T> mapping(Collection<RdbColumnMetaData> columns, ResultSet rs) {
        return orm.mapping(columns, rs) ;
    }
    
    public Collection<T> searchList(RdbQuery<T> query) {
    	return searchList(query, -1, -1);
    }
    
    public Collection<T> searchList(RdbQuery<T> query, int start, int max) {
        Collection<RdbColumnMetaData>columns = query.getSelectColumns();
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
    	RdbDaoEvent event = createRdbDaoEvent();
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
    
    RdbDaoEvent createRdbDaoEvent(String sql) {
    	return new RdbDaoEventImpl(getCallerDaoClass(), sql);
    }
    
    RdbDaoEvent createRdbDaoEvent() {
    	return new RdbDaoEventImpl(getCallerDaoClass());
    }

    protected ResultSet executeQuery(String sql) throws DaoException {
    	RdbDaoEvent event = createRdbDaoEvent(sql);
       	getExecuteHandler().handleBeforeExecuteQuery(event);
        ResultSet rs = dbm.executeQuery(sql);
        getExecuteHandler().handleAfterExecuteQuery(event);
        return rs;
    }

    protected int executeUpdate(String sql) throws DaoException {
    	RdbDaoEvent event = createRdbDaoEvent(sql);
    	getExecuteHandler().handleBeforeExecuteUpdate(event);
        int result = dbm.executeUpdate(sql);
        TransactionStateManager.getInstance().executed();
       	event.setResult(result);
       	return getExecuteHandler().handleAfterExecuteUpdate(event);
    }
    
    protected int executeUpdate(String sql, int index, InputStream in) throws DaoException {
    	RdbDaoEvent event = createRdbDaoEvent(sql);
    	getExecuteHandler().handleBeforeExecuteUpdate(event);
    	PreparedStatement stmt = dbm.preparedStatement(sql);
   		int result = BlobUtils.executeUpdate(stmt, index, in);
        TransactionStateManager.getInstance().executed();
       	event.setResult(result);
       	return getExecuteHandler().handleAfterExecuteUpdate(event);
    }
    
    protected void commit() throws DaoException {
    	RdbDaoEvent event = createRdbDaoEvent();
    	getTransactionHandler().handleBeforeCommit(event);
    	dbm.commit();
    	TransactionStateManager.getInstance().commit();
   		getTransactionHandler().handleAfterCommit(event);
    }
    
    protected void rollback() throws DaoException {
    	RdbDaoEvent event = createRdbDaoEvent();
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
    	RdbDaoEvent event = createRdbDaoEvent();
		getTransactionHandler().handleTransantionEnd(event);
    }
    
    void abortTransaction() {
    	throw new IllegalTransactionStateException("Transaction is not commit or rollback. Force execute rollback.");
    }
    
    protected void release() {
    	dbm.release();
    	RdbDaoEvent event = createRdbDaoEvent();
    	getTransactionHandler().handleRelease(event);
    }
}
