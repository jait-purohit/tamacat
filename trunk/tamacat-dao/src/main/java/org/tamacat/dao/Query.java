/*
 * Copyright (c) 2008-2012 tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import java.util.Collection;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.Table;
import org.tamacat.dao.orm.ORMappingSupport;

public interface Query<T extends ORMappingSupport>{

    //Query<T> addSelectColumn(Column column);
    Query<T> select(Collection<Column> columns);
    Query<T> select(Column... columns);

    Collection<Column> getSelectColumns();

    Query<T> addUpdateColumn(Column column);
    Query<T> addUpdateColumns(Collection<Column> columns);
    Query<T> addUpdateColumns(Column... columns);

    Collection<Column> getUpdateColumns();

    Query<ORMappingSupport> addTable(Table table);
    
    Query<T> join(Column col1, Column col2);

    Query<T> where(Search search, Sort sort);
    Query<T> and(Search search, Sort sort);
    Query<T> or(Search search, Sort sort);
    
    Query<T> where(String sql);
    Query<T> and(String sql);
    Query<T> or(String sql);
    
    Query<T> orderBy(Sort sort);
    
    String getSelectSQL();

    String getInsertSQL(T table);

    String getUpdateSQL(T table);

    String getDeleteSQL(T table);
    
    String getDeleteAllSQL(Table table);
    
    int getBlobIndex();
    
    void setUseAutoPrimaryKeyUpdate(boolean useAutoPrimaryKeyUpdate);
}
