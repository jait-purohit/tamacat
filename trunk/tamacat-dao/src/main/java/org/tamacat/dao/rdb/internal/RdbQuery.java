/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.internal;

import java.util.Collection;

import org.tamacat.dao.Search;
import org.tamacat.dao.Sort;
import org.tamacat.dao.rdb.ORMappingSupport;
import org.tamacat.dao.rdb.RdbColumnMetaData;

public interface RdbQuery<T extends ORMappingSupport>{

    RdbQuery<T> addSelectColumn(RdbColumnMetaData column);
    RdbQuery<T> addSelectColumns(Collection<RdbColumnMetaData> columns);
    Collection<RdbColumnMetaData> getSelectColumns();

    RdbQuery<T> addUpdateColumn(RdbColumnMetaData column);
    RdbQuery<T> addUpdateColumns(Collection<RdbColumnMetaData> columns);
    Collection<RdbColumnMetaData> getUpdateColumns();

    RdbQuery<T> addConnectTable(RdbColumnMetaData col1, RdbColumnMetaData col2);

    RdbQuery<T> andSearch(Search search, Sort sort);
    RdbQuery<T> orSearch(Search search, Sort sort);
    RdbQuery<T> andWhere(String sql);
    RdbQuery<T> orWhere(String sql);
    RdbQuery<T> andSort(Sort sort);
    
    String getSelectSQL();

    String getInsertSQL(T table);

    String getUpdateSQL(T table);

    String getDeleteSQL(T table);
    
    int getBlobIndex();
    
    void setUseAutoPrimaryKeyUpdate(boolean useAutoPrimaryKeyUpdate);
}
