/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

public interface RdbColumnMetaData {

    RdbColumnDefine PRIMARY_KEY = new RdbColumnDefine("primary_key");
    RdbColumnDefine FOREIGN_KEY = new RdbColumnDefine("foreign_key");
    RdbColumnDefine NOT_NULL = new RdbColumnDefine("not_null");
    RdbColumnDefine AUTO_GENERATE_ID = new RdbColumnDefine("auto_generate_id");
    RdbColumnDefine AUTO_TIMESTAMP = new RdbColumnDefine("auto_timestamp");

    String getName();

    String getColumnName();

    RdbDataType getType();

    boolean isPrimaryKey();
    boolean isNotNull();
    boolean isAutoTimestamp();
    boolean isAutoGenerateId();

    String getDefaultValue();

    RdbColumnMetaData setRdbTableMetaData(RdbTableMetaData table);
    RdbTableMetaData getRdbTableMetaData();
}
