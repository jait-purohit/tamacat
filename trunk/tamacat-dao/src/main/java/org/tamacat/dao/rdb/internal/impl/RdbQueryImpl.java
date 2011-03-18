/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.internal.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tamacat.dao.Search;
import org.tamacat.dao.Sort;
import org.tamacat.dao.rdb.Condition;
import org.tamacat.dao.rdb.ORMappingSupport;
import org.tamacat.dao.rdb.RdbColumnMetaData;
import org.tamacat.dao.rdb.RdbDataType;
import org.tamacat.dao.rdb.RdbTableMetaData;
import org.tamacat.dao.rdb.internal.RdbQuery;
import org.tamacat.dao.rdb.internal.SQLParser;
import org.tamacat.dao.rdb.util.MappingUtils;
import org.tamacat.util.UniqueCodeGenerator;

public class RdbQueryImpl<T extends ORMappingSupport> implements RdbQuery<ORMappingSupport> {

    static final String SELECT = "SELECT";
    static final String FROM = "FROM";
    static final String WHERE = "WHERE";
    static final String ORDER_BY = "ORDER BY";
    static final String INSERT = "INSERT INTO ${TABLE} (${COLUMNS}) VALUES (${VALUES})";
    static final String UPDATE = "UPDATE ${TABLE} SET ${VALUES}";
    static final String DELETE = "DELETE FROM ${TABLE}";

    Collection<RdbColumnMetaData> selectColumns = new LinkedHashSet<RdbColumnMetaData>();
    Collection<RdbColumnMetaData> updateColumns = new LinkedHashSet<RdbColumnMetaData>();
    Set<RdbTableMetaData> tables = new LinkedHashSet<RdbTableMetaData>();
    StringBuffer where = new StringBuffer();
    StringBuffer orderBy = new StringBuffer();

    int blobIndex = 0;
    
    public RdbQuery<ORMappingSupport> addSelectColumn(RdbColumnMetaData column) {
        selectColumns.add(column);
        return this;
    }

    public RdbQuery<ORMappingSupport> addSelectColumns(Collection<RdbColumnMetaData> columns) {
        selectColumns.addAll(columns);
        return this;
    }

    public Collection<RdbColumnMetaData> getSelectColumns() {
        return selectColumns;
    }

    public RdbQuery<ORMappingSupport> addUpdateColumn(RdbColumnMetaData column) {
        updateColumns.add(column);
        return this;
    }

    public RdbQuery<ORMappingSupport> addUpdateColumns(Collection<RdbColumnMetaData> columns) {
        updateColumns.addAll(columns);
        return this;
    }

    public Collection<RdbColumnMetaData> getUpdateColumns() {
        return updateColumns;
    }

    public String getSelectSQL() {
    	blobIndex = 0;
        StringBuffer select = new StringBuffer();
        for (RdbColumnMetaData col : getSelectColumns()) {
            if (select.length() == 0) {
                select.append(SELECT + " ");
            } else {
                select.append(",");
            }
            if (col.getType() == RdbDataType.OBJECT) {
            	blobIndex++;
            }
            select.append(getColumnName(col));
            tables.add(col.getRdbTableMetaData());
        }
        StringBuffer from = new StringBuffer();
        for (RdbTableMetaData tab : tables) {
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
        for (RdbColumnMetaData col : updateColumns.toArray(new RdbColumnMetaData[updateColumns.size()])) {
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
            if (col.getType() == RdbDataType.OBJECT) {
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
        for (RdbColumnMetaData col : updateColumns.toArray(new RdbColumnMetaData[updateColumns.size()])) {
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
            }
        }
        String query = UPDATE.replace("${TABLE}", tableName)
            .replace("${VALUES}", values.toString());

        return query + where.toString();
    }

    public String getDeleteSQL(ORMappingSupport data) {
        SQLParser parser = new SQLParser();
        String tableName = null;
        for (RdbColumnMetaData col : updateColumns.toArray(new RdbColumnMetaData[updateColumns.size()])) {
            if (tableName == null) tableName = col.getRdbTableMetaData().getTableName();
            if (col.isPrimaryKey()) {
                addWhere("and", parser.value(col, Condition.EQUAL, data.getValue(col)));
                continue;
            }
        }
        String query = DELETE.replace("${TABLE}", tableName);
        return query + where.toString();
    }

    public RdbQuery<ORMappingSupport> addConnectTable(RdbColumnMetaData col1, RdbColumnMetaData col2) {
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

    public RdbQuery<ORMappingSupport> andSearch(Search search, Sort sort) {
        return addSearch("and", search, sort);
    }

    public RdbQuery<ORMappingSupport> orSearch(Search search, Sort sort) {
        return addSearch("or", search, sort);
    }

    public RdbQuery<ORMappingSupport> andWhere(String sql) {
        return addWhere("and", sql);
    }

    public RdbQuery<ORMappingSupport> orWhere(String sql) {
        return addWhere("or", sql);
    }
    
    public RdbQuery<ORMappingSupport> andSort(Sort sort) {
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
    
    protected RdbQuery<ORMappingSupport> addWhere(String condition, String sql) {
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

    private RdbQuery<ORMappingSupport> addSearch(String condition, Search search, Sort sort) {
        addWhere(condition, search.getSearchString());
        return andSort(sort);
    }
    
    public String getTimestampString() {
    	return "current_timestamp";
    }
    
    public int getBlobIndex() {
    	return blobIndex;
    }
    
    static String getColumnName(RdbColumnMetaData col) {
    	return MappingUtils.getColumnName(col);
    }
}
