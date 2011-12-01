/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.test;

import java.util.Collection;

import org.tamacat.dao.Condition;
import org.tamacat.dao.RdbDaoAdapter;
import org.tamacat.dao.RdbQuery;
import org.tamacat.dao.RdbSearch;
import org.tamacat.dao.RdbSort;

public class UserDao extends RdbDaoAdapter<User> {

    public UserDao() {}

    public User search(User data) {
        RdbQuery<User> query = createQuery()
            .addSelectColumns(User.TABLE.getColumns())
            .andWhere(param(User.USER_ID, Condition.EQUAL, data.getValue(User.USER_ID)));
        return super.search(query);
    }

    public Collection<User> searchList(RdbSearch search, RdbSort sort) {
        RdbQuery<User> query = createQuery()
            .addSelectColumns(User.TABLE.getColumns()).andSearch(search, sort);
        return super.searchList(query, search.getStart(), search.getMax());
    }

    @Override
    protected String getInsertSQL(User data) {
        RdbQuery<User> query = createQuery().addUpdateColumns(User.TABLE.getColumns());
        return query.getInsertSQL(data);
    }

    @Override
    protected String getUpdateSQL(User data) {
        RdbQuery<User> query = createQuery().addUpdateColumns(User.TABLE.getColumns());
        return query.getUpdateSQL(data);
    }

    @Override
    protected String getDeleteSQL(User data) {
        RdbQuery<User> query = createQuery().addUpdateColumn(User.USER_ID);
        return query.getDeleteSQL(data);
    }
    
    public int createTable() {
    	return executeUpdate(
    		"CREATE TABLE users (user_id varchar(32),"
    		+ "password varchar(20), dept_id varchar(32))");
    }
    
    public int dropTable() {
    	return executeUpdate("DROP TABLE users");
    }
}
