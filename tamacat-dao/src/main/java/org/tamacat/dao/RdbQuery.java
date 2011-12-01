/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import java.util.Collection;

import org.tamacat.dao.meta.RdbColumnMetaData;
import org.tamacat.dao.meta.RdbTableMetaData;
import org.tamacat.dao.orm.ORMappingSupport;

public interface RdbQuery<T extends ORMappingSupport>{

    RdbQuery<T> addSelectColumn(RdbColumnMetaData column);
    RdbQuery<T> addSelectColumns(Collection<RdbColumnMetaData> columns);
    Collection<RdbColumnMetaData> getSelectColumns();

    RdbQuery<T> addUpdateColumn(RdbColumnMetaData column);
    RdbQuery<T> addUpdateColumns(Collection<RdbColumnMetaData> columns);
    Collection<RdbColumnMetaData> getUpdateColumns();

    RdbQuery<ORMappingSupport> addTable(RdbTableMetaData table);
    
    RdbQuery<T> addConnectTable(RdbColumnMetaData col1, RdbColumnMetaData col2);

    RdbQuery<T> andSearch(RdbSearch search, RdbSort sort);
    RdbQuery<T> orSearch(RdbSearch search, RdbSort sort);
    RdbQuery<T> andWhere(String sql);
    RdbQuery<T> orWhere(String sql);
    RdbQuery<T> andSort(RdbSort sort);
    
    String getSelectSQL();

    String getInsertSQL(T table);

    String getUpdateSQL(T table);

    String getDeleteSQL(T table);
    
    String getDeleteAllSQL(RdbTableMetaData table);
    
    int getBlobIndex();
    
    void setUseAutoPrimaryKeyUpdate(boolean useAutoPrimaryKeyUpdate);
}
