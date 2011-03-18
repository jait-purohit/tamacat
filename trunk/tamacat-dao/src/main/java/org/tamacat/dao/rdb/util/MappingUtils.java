/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.tamacat.dao.rdb.RdbColumnMetaData;
import org.tamacat.dao.rdb.RdbDataType;
import org.tamacat.dao.rdb.RdbTableMetaData;
import org.tamacat.util.DateUtils;

public class MappingUtils {

    public static Object mapping(RdbDataType type, ResultSet rs, int index) throws SQLException {
        switch (type) {
        case STRING:
            return rs.getString(index);
        case DATE:
        case TIME:
            return rs.getDate(index);
        case NUMERIC:
            return rs.getLong(index);
        case FLOAT:
            return rs.getDouble(index);
        case OBJECT:
            return rs.getObject(index);
        }
        return null;
    }
    
    public static String parse(
    		RdbDataType type, HashMap<String, Object> data, 
    		RdbColumnMetaData column) {
    	String key = getColumnName(column);
    	Object value = data.get(key);
    	if (value == null) return null;
    	if (value instanceof Date) {
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
    
    public static String getColumnName(RdbColumnMetaData col) {
    	RdbTableMetaData table = col.getRdbTableMetaData();
    	if (table == null) {
    		throw new IllegalArgumentException(
    			"Column [" + col.getColumnName() + "] is not registered RdbTableMetaData.");
    	}
    	return table.getTableNameWithSchema() + "." + col.getColumnName();
    }
}
