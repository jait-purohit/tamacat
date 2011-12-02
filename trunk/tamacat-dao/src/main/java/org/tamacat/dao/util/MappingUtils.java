/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.meta.Table;
import org.tamacat.util.DateUtils;

public class MappingUtils {

    public static Object mapping(DataType type, ResultSet rs, int index) throws SQLException {
        switch (type) {
        case STRING:
            return rs.getString(index);
        case DATE:
            return rs.getDate(index);
        case TIME:
            return rs.getString(index);
        case NUMERIC:
            return rs.getLong(index);
        case FLOAT:
            return rs.getDouble(index);
        case OBJECT:
            return rs.getObject(index);
        }
        return null;
    }
    
    public static String parse(Column column, Object value) {
    	DataType type = column.getType();
    	if (value == null) return null;
    	if (type == DataType.DATE && value instanceof Date) {
    		return DateUtils.getTime((Date)value, "yyyy-MM-dd"); //TODO
    	} else if (type == DataType.TIME && value instanceof Date) {
        	return DateUtils.getTime((Date)value, "yyyy-MM-dd HH:mm:ss"); //TODO
    	} else if (value instanceof String){
    		return (String) value;
    	} else {
    		return value.toString();
    	}
//        switch (type) {
//        case STRING:
//            return (String) data.get(column);
//        case DATE:
//        case TIME:
//        	Date date = (Date) data.get(column);
//        	if (date == null) return null;
//            return DateUtils.getTime(date, "yyyy-MM-dd HH:mm:ss"); //TODO
//        case NUMERIC:
//            return (String) data.get(column);
//        case FLOAT:
//            return (String) data.get(column);
//        case OBJECT:
//            return (String) data.get(column);
//        }
//        return null;
    }
    
    public static String getColumnName(Column col) {
    	Table table = col.getTablea();
    	if (table == null) {
    		throw new IllegalArgumentException(
    			"Column [" + col.getColumnName() + "] is not registered RdbTableMetaData.");
    	}
    	return table.getTableNameWithSchema() + "." + col.getColumnName();
    }
}
