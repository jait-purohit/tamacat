/*
 * Copyright (c) 2008, TamaCat.org
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
import org.tamacat.dao.meta.ColumnMetaData;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.meta.TableMetaData;
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

    Collection<ColumnMetaData> selectColumns = new LinkedHashSet<ColumnMetaData>();
    Collection<ColumnMetaData> updateColumns = new LinkedHashSet<ColumnMetaData>();
    Set<TableMetaData> tables = new LinkedHashSet<TableMetaData>();
    StringBuffer where = new StringBuffer();
    StringBuffer orderBy = new StringBuffer();

    int blobIndex = 0;
    
    public Query<ORMappingSupport> addTable(TableMetaData table) {
    	tables.add(table);
    	return this;
    }
    
    public Query<ORMappingSupport> addSelectColumn(ColumnMetaData column) {
        selectColumns.add(column);
        return this;
    }

    public Query<ORMappingSupport> addSelectColumns(Collection<ColumnMetaData> columns) {
        selectColumns.addAll(columns);
        return this;
    }

    public Collection<ColumnMetaData> getSelectColumns() {
        return selectColumns;
    }

    public Query<ORMappingSupport> addUpdateColumn(ColumnMetaData column) {
        updateColumns.add(column);
        return this;
    }

    public Query<ORMappingSupport> addUpdateColumns(Collection<ColumnMetaData> columns) {
        updateColumns.addAll(columns);
        return this;
    }

    public Collection<ColumnMetaData> getUpdateColumns() {
        return updateColumns;
    }

    public String getSelectSQL() {
    	blobIndex = 0;
        StringBuffer select = new StringBuffer();
        for (ColumnMetaData col : getSelectColumns()) {
            if (select.length() == 0) {
                select.append(SELECT + " ");
            } else {
                select.append(",");
            }
            if (col.getType() == DataType.OBJECT) {
            	blobIndex++;
            }
            select.append(getColumnName(col));
            tables.add(col.getRdbTableMetaData());
        }
        StringBuffer from = new StringBuffer();
        for (TableMetaData tab : tables) {
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
        for (ColumnMetaData col : updateColumns.toArray(new ColumnMetaData[updateColumns.size()])) {
            if (tableName == null) tableName = col.getRdbTableMetaData().getTableName();
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
        for (ColumnMetaData col : updateColumns.toArray(new ColumnMetaData[updateColumns.size()])) {
            if (tableName == null) tableName = col.getRdbTableMetaData().getTableName();
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
        	for (ColumnMetaData col : updateColumns.toArray(new ColumnMetaData[updateColumns.size()])) {
            	if (tableName == null) tableName = col.getRdbTableMetaData().getTableName();
            	if (! useAutoPrimaryKeyUpdate) {
            		addWhere("and", parser.value(col, Condition.EQUAL, data.getValue(col)));
            	} else if (col.isPrimaryKey()) {
                	addWhere("and", parser.value(col, Condition.EQUAL, data.getValue(col)));
            	}
        	}
        }
        if (tableName == null) {
        	for (TableMetaData table : tables) {
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
    
    public String getDeleteAllSQL(TableMetaData table) {
        String tableName = table.getTableName();
        String query = DELETE.replace("${TABLE}", tableName);
        return query + where.toString();
    }

    public Query<ORMappingSupport> addConnectTable(ColumnMetaData col1, ColumnMetaData col2) {
        tables.add(col1.getRdbTableMetaData());
        tables.add(col2.getRdbTableMetaData());
        if (where.length() == 0) {
            where.append(" " + WHERE + " ");
        } else {
            where.append(" and ");
        }
        where.append(getColumnName(col1) + "=" + getColumnName(col2));
        return this;
    }

    public Query<ORMappingSupport> andSearch(Search search, Sort sort) {
        return addSearch("and", search, sort);
    }

    public Query<ORMappingSupport> orSearch(Search search, Sort sort) {
        return addSearch("or", search, sort);
    }

    public Query<ORMappingSupport> andWhere(String sql) {
        return addWhere("and", sql);
    }

    public Query<ORMappingSupport> orWhere(String sql) {
        return addWhere("or", sql);
    }
    
    public Query<ORMappingSupport> andSort(Sort sort) {
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
        return andSort(sort);
    }
    
    public String getTimestampString() {
    	return "current_timestamp";
    }
    
    public int getBlobIndex() {
    	return blobIndex;
    }
    
    static String getColumnName(ColumnMetaData col) {
    	return MappingUtils.getColumnName(col);
    }
}
