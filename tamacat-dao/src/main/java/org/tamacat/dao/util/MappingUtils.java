/*
 * Copyright (c) 2008-2012 tamacat.org
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

	static String DATE_FORMAT = "yyyy-MM-dd";
	static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
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
		case FUNCTION:
			break;
		default:
			break;
        }
        return null;
    }
    
    public static String parse(Column column, Object value) {
    	DataType type = column.getType();
    	if (value == null) return null;
    	if (type == DataType.DATE && value instanceof Date) {
    		return DateUtils.getTime((Date)value, DATE_FORMAT);
    	} else if (type == DataType.TIME && value instanceof Date) {
        	return DateUtils.getTime((Date)value, TIME_FORMAT);
    	} else if (value instanceof String){
    		return (String) value;
    	} else {
    		return value.toString();
    	}
    }
    
    public static String getColumnName(Column col) {
    	Table table = col.getTable();
    	if (table == null) {
    		throw new IllegalArgumentException(
    			"Column [" + col.getColumnName() + "] is not registered RdbTableMetaData.");
    	}
    	return table.getTableNameWithSchema() + "." + col.getColumnName();
    }
}
