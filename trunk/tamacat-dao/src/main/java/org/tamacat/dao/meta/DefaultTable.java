/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class DefaultTable implements Table {

    private LinkedHashSet<Column> columns = new LinkedHashSet<>();
    private HashSet<Column> primaryKeys = new HashSet<>();

    private String schemaName;
    private String tableName;
    private String aliasName;

    public DefaultTable(String... name) {
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
    public Collection<Column> getColumns() {
        return (Collection<Column>) columns.clone();
    }
    
    public Column[] columns() {
    	return columns.toArray(new Column[columns.size()]);
    }

    @Override
    public Collection<Column> getPrimaryKeys() {
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

    public DefaultTable setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DefaultTable setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    @Override
    public Table registerColumn(Column... cols) {
        for (Column column : cols) {
            if (column.isPrimaryKey()) this.primaryKeys.add(column);
            this.columns.add(column);
            column.setTable(this);
        }
        return this;
    }
    
    public boolean equalsTable(Object target) {
    	if (target == null) return false;
    	if (target instanceof Column) {
    		return equals(((Column)target).getTable());
    	} else if (target instanceof Table) {
    		return equals(((Table)target));
    	} else {
    		return equals(target);
    	}
    }
    
    @Override
    public Column find(String columnName) {
    	for (Column col : columns) {
    		if (col.getColumnName().equalsIgnoreCase(columnName)) {
    			return col;
    		}
    	}
    	return null;
    }
}
