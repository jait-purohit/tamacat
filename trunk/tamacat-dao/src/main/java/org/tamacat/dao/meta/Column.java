/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;


public interface Column {

    ColumnDefine PRIMARY_KEY = ColumnDefine.PRIMARY_KEY;
    ColumnDefine FOREIGN_KEY = ColumnDefine.FOREIGN_KEY;
    ColumnDefine NOT_NULL = ColumnDefine.NOT_NULL;
    ColumnDefine AUTO_GENERATE_ID = ColumnDefine.AUTO_GENERATE_ID;
    ColumnDefine AUTO_TIMESTAMP = ColumnDefine.AUTO_TIMESTAMP;

    String getName();

    String getColumnName();

    DataType getType();

    boolean isPrimaryKey();
    boolean isNotNull();
    boolean isAutoTimestamp();
    boolean isAutoGenerateId();

    String getDefaultValue();

    Column setTable(Table table);
    Table getTablea();
}
