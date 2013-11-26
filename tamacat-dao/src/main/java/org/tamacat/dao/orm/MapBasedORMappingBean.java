/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao.orm;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.util.MappingUtils;
import org.tamacat.util.StringUtils;

/**
 * Map based ORMaping bean.
 * (extends LinkedHashMap)
 */
public class MapBasedORMappingBean extends LinkedHashMap<String, Object> implements ORMappingSupport {

    private static final long serialVersionUID = 1L;
    protected Set<String> updated = new LinkedHashSet<>();
    protected GetFilter getfilter;
    protected SetFilter setfilter;
    
    public String getValue(Column column) {
    	return val(column);
    }
    
    public String val(Column column) {
    	return MappingUtils.parse(column, super.get(MappingUtils.getColumnName(column)));
    }
    
    public MapBasedORMappingBean setValue(Column column, String value) {
        return val(column, value);
    }
    
    public MapBasedORMappingBean val(Column column, Object value) {
        put(MappingUtils.getColumnName(column), value);
        return this;
    }
    
    @Override
    public Object get(Object name) {
   		if (getfilter != null && name instanceof String) {
   			return getfilter.get((String)name);
    	} else {
    		return originalGet(name);
    	}
    }
    
    protected Object originalGet(Object name) {
    	return super.get(name);
    }
    
    @Override
    public Object put(String name, Object value) {
    	if (setfilter != null) {
    		return setfilter.put(name, value);
    	} else {
    		return originalPut(name, value);
    	}
    }

    protected Object originalPut(String name, Object value) {
    	updated.add(name);
    	return super.put(name, value);
    }
    
    public MapBasedORMappingBean mapping(Object name, Object value) {
    	String val = value != null ? value.toString() : "";
        put(parse(name), val);
        return this;
    }

    public boolean isUpdate(Object name) {
        if (name instanceof Column) {
            return updated.contains(MappingUtils.getColumnName((Column)name));
        } else {
            return updated.contains(name);
        }
    }
    
    public boolean isEmpty(Column column) {
    	return StringUtils.isEmpty(val(column));
    }
    
    public boolean isNotEmpty(Column column) {
    	return StringUtils.isNotEmpty(val(column));
    }
    
    public static String parse(Object data) {
        if (data instanceof Column) {
            return (MappingUtils.getColumnName((Column)data));
        } else {
            return data.toString();
        }
    }
    
    protected boolean startsWith(String target, String prefix) {
    	return target != null && target.startsWith(prefix);
    }
    
    /**
     * Set the KeyValueFilter.
     * @param filter
     */
    protected void setKeyValueFilter(KeyValueFilter filter) {
    	if (filter instanceof GetFilter) {
    		this.getfilter = (GetFilter)filter;
    	}
    	if (filter instanceof SetFilter) {
    		this.setfilter = (SetFilter)filter;
    	}
    }
    
    /**
     * Marker interface for Get/Set Filter.
     */
    protected static interface KeyValueFilter {} 
    
    /**
     * Interceptor for Map#get method.
     */
    protected static interface GetFilter extends KeyValueFilter {
    	Object get(String name);
    }
    
    /**
     * Interceptor for Map#put method.
     */
    protected static interface SetFilter extends KeyValueFilter {
    	Object put(String name, Object value);
    }
}
