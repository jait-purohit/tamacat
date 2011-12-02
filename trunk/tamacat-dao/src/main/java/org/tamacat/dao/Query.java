/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import java.util.Collection;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.Table;
import org.tamacat.dao.orm.ORMappingSupport;

public interface Query<T extends ORMappingSupport>{

    Query<T> addSelectColumn(Column column);
    Query<T> addSelectColumns(Collection<Column> columns);
    Collection<Column> getSelectColumns();

    Query<T> addUpdateColumn(Column column);
    Query<T> addUpdateColumns(Collection<Column> columns);
    Collection<Column> getUpdateColumns();

    Query<ORMappingSupport> addTable(Table table);
    
    Query<T> addConnectTable(Column col1, Column col2);

    Query<T> andSearch(Search search, Sort sort);
    Query<T> orSearch(Search search, Sort sort);
    Query<T> andWhere(String sql);
    Query<T> orWhere(String sql);
    Query<T> andSort(Sort sort);
    
    String getSelectSQL();

    String getInsertSQL(T table);

    String getUpdateSQL(T table);

    String getDeleteSQL(T table);
    
    String getDeleteAllSQL(Table table);
    
    int getBlobIndex();
    
    void setUseAutoPrimaryKeyUpdate(boolean useAutoPrimaryKeyUpdate);
}
