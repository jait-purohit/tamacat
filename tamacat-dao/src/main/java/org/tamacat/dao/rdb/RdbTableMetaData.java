/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import java.util.Collection;

public interface RdbTableMetaData {

    String getSchemaName();

    String getTableName();

    String getTableOrAliasName();

    String getTableNameWithSchema();

    Collection<RdbColumnMetaData> getPrimaryKeys();
    Collection<RdbColumnMetaData> getColumns();

    RdbTableMetaData registerColumn(RdbColumnMetaData... columns);

    RdbColumnMetaData find(String columnName);
}
