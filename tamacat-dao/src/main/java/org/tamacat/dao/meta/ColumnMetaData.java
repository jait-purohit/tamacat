/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;


public interface ColumnMetaData {

    ColumnDefine PRIMARY_KEY = new ColumnDefine("primary_key");
    ColumnDefine FOREIGN_KEY = new ColumnDefine("foreign_key");
    ColumnDefine NOT_NULL = new ColumnDefine("not_null");
    ColumnDefine AUTO_GENERATE_ID = new ColumnDefine("auto_generate_id");
    ColumnDefine AUTO_TIMESTAMP = new ColumnDefine("auto_timestamp");

    String getName();

    String getColumnName();

    DataType getType();

    boolean isPrimaryKey();
    boolean isNotNull();
    boolean isAutoTimestamp();
    boolean isAutoGenerateId();

    String getDefaultValue();

    ColumnMetaData setRdbTableMetaData(TableMetaData table);
    TableMetaData getRdbTableMetaData();
}
