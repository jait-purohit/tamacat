/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;



public class DefaultRdbColumnMetaData implements ColumnMetaData {

    String columnName;
    String defaultValue;
    String name;
    DataType type;
    boolean isAutoGenerateId;
    boolean isAutoTimestamp;
    boolean isNotNull;
    boolean isPrimaryKey;
    TableMetaData table;

    public DefaultRdbColumnMetaData() {}

    public DefaultRdbColumnMetaData(
            TableMetaData table, String columnName, DataType type,
            String name, ColumnDefine... defines) {
        this.columnName = columnName;
        this.type = type;
        this.name = name;
        this.table = table;
        table.registerColumn(this);
        if (defines != null) {
            for (ColumnDefine def : defines) {
                if (PRIMARY_KEY.equals(def)) this.isPrimaryKey = true;
                if (AUTO_GENERATE_ID.equals(def)) this.isAutoGenerateId = true;
                if (AUTO_TIMESTAMP.equals(def)) this.isAutoTimestamp = true;
                if (NOT_NULL.equals(def)) this.isNotNull = true;
            }
        }
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public DefaultRdbColumnMetaData setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public DefaultRdbColumnMetaData setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    public boolean isAutoGenerateId() {
        return isAutoGenerateId;
    }
    public DefaultRdbColumnMetaData setAutoGenerateId(boolean isAutoGenerateId) {
        this.isAutoGenerateId = isAutoGenerateId;
        return this;
    }
    public boolean isAutoTimestamp() {
        return isAutoTimestamp;
    }
    public DefaultRdbColumnMetaData setAutoTimestamp(boolean isAutoTimestamp) {
        this.isAutoTimestamp = isAutoTimestamp;
        return this;
    }
    public boolean isNotNull() {
        return isNotNull;
    }
    public DefaultRdbColumnMetaData setNotNull(boolean isNotNull) {
        this.isNotNull = isNotNull;
        return this;
    }
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    public DefaultRdbColumnMetaData setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        return this;
    }
    public String getName() {
        return name;
    }
    public DefaultRdbColumnMetaData setName(String name) {
        this.name = name;
        return this;
    }
    public DataType getType() {
        return type;
    }
    public DefaultRdbColumnMetaData setType(DataType type) {
        this.type = type;
        return this;
    }
    public ColumnMetaData setRdbTableMetaData(TableMetaData table) {
        this.table = table;
        return this;
    }
    public TableMetaData getRdbTableMetaData() {
        return table;
    }
}
