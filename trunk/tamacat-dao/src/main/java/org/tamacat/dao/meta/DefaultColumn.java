/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;

public class DefaultColumn implements Column {

    String columnName;
    String defaultValue;
    String name;
    DataType type;
    boolean isAutoGenerateId;
    boolean isAutoTimestamp;
    boolean isNotNull;
    boolean isPrimaryKey;
    Table table;

    public DefaultColumn() {}
    
    public DefaultColumn(String columnName) {
    	this.columnName = columnName;
    }
    
    public DefaultColumn(
            Table table, String columnName, DataType type,
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
    
    public DefaultColumn setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }
    public DefaultColumn column(String columnName) {
        this.columnName = columnName;
        return this;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public DefaultColumn setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    public boolean isAutoGenerateId() {
        return isAutoGenerateId;
    }
    public DefaultColumn setAutoGenerateId(boolean isAutoGenerateId) {
        this.isAutoGenerateId = isAutoGenerateId;
        return this;
    }
    
    public boolean isAutoTimestamp() {
        return isAutoTimestamp;
    }
    public DefaultColumn setAutoTimestamp(boolean isAutoTimestamp) {
        this.isAutoTimestamp = isAutoTimestamp;
        return this;
    }
    public boolean isNotNull() {
        return isNotNull;
    }
    public DefaultColumn setNotNull(boolean isNotNull) {
        this.isNotNull = isNotNull;
        return this;
    }
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    public DefaultColumn setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        return this;
    }
    public String getName() {
        return name;
    }
    public DefaultColumn setName(String name) {
        this.name = name;
        return this;
    }
    public DataType getType() {
        return type;
    }
    public DefaultColumn setType(DataType type) {
        this.type = type;
        return this;
    }
    public DefaultColumn type(DataType type) {
        this.type = type;
        return this;	
    }
    
    public Column setTable(Table table) {
        this.table = table;
        return this;
    }
    public Table getTable() {
        return table;
    }
}
