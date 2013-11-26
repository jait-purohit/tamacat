/*
 * Copyright (c) 2007, tamacat.org
 * All rights reserved.
 */
package org.tamacat.sql;

import org.tamacat.dao.Condition;
import org.tamacat.dao.Search;
import org.tamacat.dao.Search.ValueConvertFilter;
import org.tamacat.dao.exception.InvalidParameterException;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.util.MappingUtils;

public class SQLParser {

    public static final String VALUE1 = "#{value1}";
    public static final String VALUE2 = "#{value2}";
    public static final String MULTI_VALUE = "#{values}";
    public static final String[] VALUES = { VALUE1, VALUE2 };

    protected static final String ESCAPE = " escape '?'";

    ValueConvertFilter valueConvertFilter;

    public SQLParser() {
    	this.valueConvertFilter = new Search.DefaultValueConvertFilter();
    }

    public SQLParser(ValueConvertFilter valueConvertFilter) {
        this.valueConvertFilter = valueConvertFilter;
    }

    public String value(Column column, Condition condition, String... values) {
    	String colName = MappingUtils.getColumnName(column);
        StringBuffer search = new StringBuffer(colName + condition.getCondition());
        if (values != null) {
            if (values.length == 1){
            	String value = values[0];
            	if (value == null) throw new InvalidParameterException("Column [" + colName + "] is required.");
            	
                if (column.getType() == DataType.STRING //for LIKE 'String Data'
                  && condition.getCondition().indexOf(" like ") >= 0) {
                    search.append(parseLikeStringValue(condition, column, value));
                } else if (condition.getCondition().equals(" in ")) { //IN
                	search.append(parseValue(column, condition.getReplaceHolder().replace(MULTI_VALUE, value)));
                } else {
                    search.append(parseValue(column, condition.getReplaceHolder().replace(VALUE1, value)));
                }
            } else if (values.length >= 2) {
                String v = condition.getReplaceHolder(); //for BETWEEN
                if (condition.getCondition().indexOf(" between ") >= 0) {
                    for (int i=0; i<values.length; i++) {
                        v = v.replace(VALUES[i], parseValue(column, values[i]));
                    }
                } else { // for IN
                	v = parseMultiValue(column, v, values);
                }
                search.append(v);
            }
        }
        return search.toString();
    }

    String parseMultiValue(Column column, String v, String... values) {
        StringBuffer parsed = new StringBuffer();
        for (int i=0; i<values.length; i++) {
            if (parsed.length() > 0) parsed.append(",");
            parsed.append(parseValue(column, values[i]));
        }
        return v.replace(MULTI_VALUE, parsed.toString());
    }
    
    public String parseValue(Column column, String value) {
        String parseValue = (valueConvertFilter == null)? value : valueConvertFilter.convertValue(value);
        if (column.getType() == DataType.STRING) {
        	if (value == null) return parseValue;
            return "'" + parseValue + "'";
        } else if (column.getType() == DataType.TIME || column.getType() == DataType.DATE) {
        	if (value == null) return parseValue;
        	if (value.equalsIgnoreCase("current_timestamp")) { //TODO
        		return parseValue;
        	} else {
        		return "'" + parseValue + "'";
        	}
        } else if (column.getType() == DataType.OBJECT) {
        	return "?";
        } else {
            return parseValue;
        }
    }

    String parseLikeStringValue(Condition condition, Column column, String value) {
        if (value.indexOf('%') >= 0 || value.indexOf('_') >= 0) {
            char[] esc = new char[]{'$', '#', '~', '!', '^'};
            for (char e : esc) {
                if (value.indexOf(e) == -1) {
                    String val = value.replace("%", e + "%").replace("_", e + "_");
                    val = condition.getReplaceHolder().replace(VALUE1, val);
                    String parseValue = (valueConvertFilter == null)? val : valueConvertFilter.convertValue(val);
                    if (column.getType() == DataType.STRING) {
                        parseValue = "'" + parseValue + "'";
                    }
                    return parseValue + ESCAPE.replace('?', e);
                }
            }
        }
        return parseValue(column, condition.getReplaceHolder().replace(VALUE1, value));
    }
}
