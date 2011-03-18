/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tamacat.dao.rdb.util.MappingUtils;

public class MapBasedORMappingBean extends HashMap<String, Object> implements ORMappingSupport {

    private static final long serialVersionUID = 1L;
    protected Set<String> updated = new LinkedHashSet<String>();

    public String getValue(RdbColumnMetaData column) {
    	return MappingUtils.parse(column.getType(), this, column);
    	//return (String) super.get(MappingUtils.getColumnName(column));
    }
    
    public MapBasedORMappingBean setValue(RdbColumnMetaData column, String value) {
        put(MappingUtils.getColumnName(column), value);
        return this;
    }
    
    @Override
    public Object put(String name, Object value) {
        updated.add(name);
        return super.put(name, value);
    }

    public MapBasedORMappingBean mapping(Object name, Object value) {
    	String val = value != null ? value.toString() : "";
        put(parse(name), val);
        return this;
    }

    public boolean isUpdate(Object name) {
        if (name instanceof RdbColumnMetaData) {
            return updated.contains(MappingUtils.getColumnName((RdbColumnMetaData)name));
        } else {
            return updated.contains(name);
        }
    }
    
    public static String parse(Object data) {
        if (data instanceof RdbColumnMetaData) {
            return (MappingUtils.getColumnName((RdbColumnMetaData)data));
        } else {
            return data.toString();
        }
    }
}
