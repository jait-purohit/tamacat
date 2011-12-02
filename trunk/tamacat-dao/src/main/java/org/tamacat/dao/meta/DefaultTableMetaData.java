/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class DefaultTableMetaData implements TableMetaData {

    private LinkedHashSet<ColumnMetaData> columns = new LinkedHashSet<ColumnMetaData>();
    private HashSet<ColumnMetaData> primaryKeys = new HashSet<ColumnMetaData>();

    private String schemaName;
    private String tableName;
    private String aliasName;

    public DefaultTableMetaData(String... name) {
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
    public Collection<ColumnMetaData> getColumns() {
        return (Collection<ColumnMetaData>) columns.clone();
    }

    @Override
    public Collection<ColumnMetaData> getPrimaryKeys() {
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

    public DefaultTableMetaData setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DefaultTableMetaData setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    @Override
    public TableMetaData registerColumn(ColumnMetaData... cols) {
        for (ColumnMetaData column : cols) {
            if (column.isPrimaryKey()) this.primaryKeys.add(column);
            this.columns.add(column);
            column.setRdbTableMetaData(this);
        }
        return this;
    }
    
    public boolean equalsTable(Object target) {
    	if (target == null) return false;
    	if (target instanceof ColumnMetaData) {
    		return equals(((ColumnMetaData)target).getRdbTableMetaData());
    	} else if (target instanceof TableMetaData) {
    		return equals(((TableMetaData)target));
    	} else {
    		return equals(target);
    	}
    }
    
    @Override
    public ColumnMetaData find(String columnName) {
    	for (ColumnMetaData col : columns) {
    		if (col.getColumnName().equalsIgnoreCase(columnName)) {
    			return col;
    		}
    	}
    	return null;
    }
}
