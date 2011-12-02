/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;

import java.util.Collection;

public interface TableMetaData {

    String getSchemaName();

    String getTableName();

    String getTableOrAliasName();

    String getTableNameWithSchema();

    Collection<ColumnMetaData> getPrimaryKeys();
    Collection<ColumnMetaData> getColumns();

    TableMetaData registerColumn(ColumnMetaData... columns);

    ColumnMetaData find(String columnName);
}
