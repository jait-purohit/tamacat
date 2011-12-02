/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import java.util.Collection;

import org.tamacat.dao.meta.ColumnMetaData;
import org.tamacat.dao.meta.TableMetaData;
import org.tamacat.dao.orm.ORMappingSupport;

public interface Query<T extends ORMappingSupport>{

    Query<T> addSelectColumn(ColumnMetaData column);
    Query<T> addSelectColumns(Collection<ColumnMetaData> columns);
    Collection<ColumnMetaData> getSelectColumns();

    Query<T> addUpdateColumn(ColumnMetaData column);
    Query<T> addUpdateColumns(Collection<ColumnMetaData> columns);
    Collection<ColumnMetaData> getUpdateColumns();

    Query<ORMappingSupport> addTable(TableMetaData table);
    
    Query<T> addConnectTable(ColumnMetaData col1, ColumnMetaData col2);

    Query<T> andSearch(Search search, Sort sort);
    Query<T> orSearch(Search search, Sort sort);
    Query<T> andWhere(String sql);
    Query<T> orWhere(String sql);
    Query<T> andSort(Sort sort);
    
    String getSelectSQL();

    String getInsertSQL(T table);

    String getUpdateSQL(T table);

    String getDeleteSQL(T table);
    
    String getDeleteAllSQL(TableMetaData table);
    
    int getBlobIndex();
    
    void setUseAutoPrimaryKeyUpdate(boolean useAutoPrimaryKeyUpdate);
}
