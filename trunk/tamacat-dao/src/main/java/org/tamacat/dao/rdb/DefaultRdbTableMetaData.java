/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class DefaultRdbTableMetaData implements RdbTableMetaData {

    private LinkedHashSet<RdbColumnMetaData> columns = new LinkedHashSet<RdbColumnMetaData>();
    private HashSet<RdbColumnMetaData> primaryKeys = new HashSet<RdbColumnMetaData>();

    private String schemaName;
    private String tableName;
    private String aliasName;

    public DefaultRdbTableMetaData(String... name) {
        switch (name.length) {
            case 3:
                schemaName = name[0];
                tableName = name[1];
                aliasName = name[2];
                break;
            case 2:
                tableName = name[0];
                aliasName = name[1];
                break;
            case 1:
                tableName = name[0];
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public Collection<RdbColumnMetaData> getColumns() {
        return (Collection<RdbColumnMetaData>) columns.clone();
    }

    @Override
    public Collection<RdbColumnMetaData> getPrimaryKeys() {
        return primaryKeys;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getTableOrAliasName() {
        if (aliasName != null) return aliasName;
        else return tableName;
    }

    @Override
    public String getSchemaName() {
        if (schemaName == null) return "";
        else return schemaName;
    }

    @Override
    public String getTableNameWithSchema() {
        if (schemaName != null) {
            return schemaName + "." + getTableOrAliasName();
        } else {
            return getTableOrAliasName();
        }
    }

    public DefaultRdbTableMetaData setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DefaultRdbTableMetaData setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    @Override
    public RdbTableMetaData registerColumn(RdbColumnMetaData... cols) {
        for (RdbColumnMetaData column : cols) {
            if (column.isPrimaryKey()) this.primaryKeys.add(column);
            this.columns.add(column);
            column.setRdbTableMetaData(this);
        }
        return this;
    }
    
    public boolean equalsTable(Object target) {
    	if (target == null) return false;
    	if (target instanceof RdbColumnMetaData) {
    		return equals(((RdbColumnMetaData)target).getRdbTableMetaData());
    	} else if (target instanceof RdbTableMetaData) {
    		return equals(((RdbTableMetaData)target));
    	} else {
    		return equals(target);
    	}
    }
    
    @Override
    public RdbColumnMetaData find(String columnName) {
    	for (RdbColumnMetaData col : columns) {
    		if (col.getColumnName().equalsIgnoreCase(columnName)) {
    			return col;
    		}
    	}
    	return null;
    }
}
