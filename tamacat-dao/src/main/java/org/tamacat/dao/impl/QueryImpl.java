/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tamacat.dao.Condition;
import org.tamacat.dao.Query;
import org.tamacat.dao.Search;
import org.tamacat.dao.Sort;
import org.tamacat.dao.exception.InvalidParameterException;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.meta.Table;
import org.tamacat.dao.orm.ORMappingSupport;
import org.tamacat.dao.util.MappingUtils;
import org.tamacat.sql.SQLParser;
import org.tamacat.util.UniqueCodeGenerator;

public class QueryImpl<T extends ORMappingSupport> implements Query<ORMappingSupport> {

    static final String SELECT = "SELECT";
    static final String FROM = "FROM";
    static final String WHERE = "WHERE";
    static final String ORDER_BY = "ORDER BY";
    static final String INSERT = "INSERT INTO ${TABLE} (${COLUMNS}) VALUES (${VALUES})";
    static final String UPDATE = "UPDATE ${TABLE} SET ${VALUES}";
    static final String DELETE = "DELETE FROM ${TABLE}";

    Collection<Column> selectColumns = new LinkedHashSet<>();
    Collection<Column> updateColumns = new LinkedHashSet<>();
    Set<Table> tables = new LinkedHashSet<>();
    StringBuffer where = new StringBuffer();
    StringBuffer orderBy = new StringBuffer();

    int blobIndex = 0;
    
    public Query<ORMappingSupport> addTable(Table table) {
    	tables.add(table);
    	return this;
    }

    public Query<ORMappingSupport> select(Collection<Column> columns) {
        selectColumns.addAll(columns);
        return this;
    }
    
    public Query<ORMappingSupport> select(Column... columns) {
    	for (Column column : columns) {
    		selectColumns.add(column);
    	}
        return this;
    }

    public Collection<Column> getSelectColumns() {
        return selectColumns;
    }

    public Query<ORMappingSupport> addUpdateColumn(Column column) {
        updateColumns.add(column);
        return this;
    }

    public Query<ORMappingSupport> addUpdateColumns(Collection<Column> columns) {
        updateColumns.addAll(columns);
        return this;
    }
    
    public Query<ORMappingSupport> addUpdateColumns(Column... columns) {
    	for (Column column : columns) {
    		updateColumns.add(column);
    	}
        return this;
    }
    
    public Collection<Column> getUpdateColumns() {
        return updateColumns;
    }

    public String getSelectSQL() {
    	blobIndex = 0;
        StringBuffer select = new StringBuffer();
        for (Column col : getSelectColumns()) {
            if (select.length() == 0) {
                select.append(SELECT + " ");
            } else {
                select.append(",");
            }
            if (col.getType() == DataType.OBJECT) {
            	blobIndex++;
            }
            select.append(getColumnName(col));
            tables.add(col.getTable());
        }
        StringBuffer from = new StringBuffer();
        for (Table tab : tables) {
            if (from.length() == 0) {
                from.append(" " + FROM + " ");
            } else {
                from.append(",");
            }
            from.append(tab.getTableNameWithSchema());
        }
        return select.toString() + from.toString() + where.toString() + orderBy.toString();
    }

    public String getInsertSQL(ORMappingSupport data) {
        SQLParser parser = new SQLParser();
        StringBuffer columns = new StringBuffer();
        StringBuffer values = new StringBuffer();
        blobIndex = 0;
        String tableName = null;
        for (Column col : updateColumns.toArray(new Column[updateColumns.size()])) {
            if (tableName == null) tableName = col.getTable().getTableName();
            if (columns.length() > 0) {
                columns.append(",");
                values.append(",");
            }
            columns.append(col.getColumnName());
            if (data.isUpdate(col)) {
                values.append(parser.parseValue(col, data.getValue(col)));
            } else {
	            if (col.isAutoGenerateId()) {
	            	String id = UniqueCodeGenerator.generate();
	                values.append(parser.parseValue(col, id));
	                data.setValue(col, id);
	            } else if (col.isAutoTimestamp()) {
	            	values.append(parser.parseValue(col, getTimestampString()));
	            } else {
	            	values.append(parser.parseValue(col, data.getValue(col)));
	            }
            }
            if (col.getType() == DataType.OBJECT) {
            	blobIndex++;
            }
        }
        String query = INSERT.replace("${TABLE}", tableName)
            .replace("${COLUMNS}", columns.toString())
            .replace("${VALUES}", values.toString());
        return query;
    }
    
    boolean useAutoPrimaryKeyUpdate = false;

	public void setUseAutoPrimaryKeyUpdate(boolean useAutoPrimaryKeyUpdate) {
		this.useAutoPrimaryKeyUpdate = useAutoPrimaryKeyUpdate;
	}

	public String getUpdateSQL(ORMappingSupport data) {
        SQLParser parser = new SQLParser();
        StringBuffer values = new StringBuffer();
        String tableName = null;
        blobIndex = 0;
        for (Column col : updateColumns.toArray(new Column[updateColumns.size()])) {
            if (tableName == null) tableName = col.getTable().getTableName();
            if (col.isPrimaryKey()) {
            	if (useAutoPrimaryKeyUpdate) {
            		addWhere("and", parser.value(col, Condition.EQUAL, data.getValue(col)));
            	}
                continue;
            }
            if (col.isAutoGenerateId()) continue;
            
            if (data.isUpdate(col)) {
                if (values.length() > 0) {
                    values.append(",");
                }
                values.append(parser.value(col, Condition.EQUAL, data.getValue(col))
                		.replaceFirst(tableName+".", ""));
            } else if (col.isAutoTimestamp()) {
            	if (values.length() > 0) {
                    values.append(",");
                }
            	values.append(parser.value(col, Condition.EQUAL, getTimestampString())
            			.replaceFirst(tableName+".", ""));
            } else if (col.getType() == DataType.OBJECT) {
            	if (values.length() > 0) {
                    values.append(",");
                }
            	values.append(parser.value(col, Condition.EQUAL, "?"));
            	blobIndex++;
            }
        }
        String query = UPDATE.replace("${TABLE}", tableName)
            .replace("${VALUES}", values.toString());

        return query + where.toString();
    }

    public String getDeleteSQL(ORMappingSupport data) {
        SQLParser parser = new SQLParser();
        String tableName = null;
        if (updateColumns != null) {
        	for (Column col : updateColumns.toArray(new Column[updateColumns.size()])) {
            	if (tableName == null) tableName = col.getTable().getTableName();
            	if (! useAutoPrimaryKeyUpdate) {
            		addWhere("and", parser.value(col, Condition.EQUAL, data.getValue(col)));
            	} else if (col.isPrimaryKey()) {
                	addWhere("and", parser.value(col, Condition.EQUAL, data.getValue(col)));
            	}
        	}
        }
        if (tableName == null) {
        	for (Table table : tables) {
        		tableName = table.getTableName();
        		break;
        	}
            if (tableName == null) {
            	throw new InvalidParameterException();
            }
        }
        String query = DELETE.replace("${TABLE}", tableName);
        return query + where.toString();
    }
    
    public String getDeleteAllSQL(Table table) {
        String tableName = table.getTableName();
        String query = DELETE.replace("${TABLE}", tableName);
        return query + where.toString();
    }

    public Query<ORMappingSupport> join(Column col1, Column col2) {
        tables.add(col1.getTable());
        tables.add(col2.getTable());
        if (where.length() == 0) {
            where.append(" " + WHERE + " ");
        } else {
            where.append(" and ");
        }
        where.append(getColumnName(col1) + "=" + getColumnName(col2));
        return this;
    }

    public Query<ORMappingSupport> where(Search search, Sort sort) {
        return addSearch("and", search, sort);
    }
    
    public Query<ORMappingSupport> and(Search search, Sort sort) {
        return addSearch("and", search, sort);
    }

    public Query<ORMappingSupport> or(Search search, Sort sort) {
        return addSearch("or", search, sort);
    }

    public Query<ORMappingSupport> where(String sql) {
        return addWhere("and", sql);
    }
    
    public Query<ORMappingSupport> and(String sql) {
        return addWhere("and", sql);
    }

    public Query<ORMappingSupport> or(String sql) {
        return addWhere("or", sql);
    }
    
    public Query<ORMappingSupport> orderBy(Sort sort) {
    	if (sort.getSortString().length() > 0) {
            if (orderBy.length() == 0) {
                orderBy.append(" " + ORDER_BY + " ");
            } else {
                orderBy.append(",");
            }
            orderBy.append(sort.getSortString());
        }
    	return this;
    }
    
    protected Query<ORMappingSupport> addWhere(String condition, String sql) {
    	if (sql != null && sql.trim().length() > 0) {
	        if (where.length() == 0) {
	            where.append(" " + WHERE + " ");
	        } else {
	            where.append(" " + condition + " ");
	        }
	        where.append(sql);
    	}
        return this;
    }

    private Query<ORMappingSupport> addSearch(String condition, Search search, Sort sort) {
        addWhere(condition, search.getSearchString());
        return orderBy(sort);
    }
    
    public String getTimestampString() {
    	return "current_timestamp";
    }
    
    public int getBlobIndex() {
    	return blobIndex;
    }
    
    static String getColumnName(Column col) {
    	return MappingUtils.getColumnName(col);
    }
}
