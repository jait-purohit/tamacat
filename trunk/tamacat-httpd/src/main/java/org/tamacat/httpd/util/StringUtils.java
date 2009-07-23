/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

public class StringUtils {
    private static final String EMPTY = "";

    static
      public boolean isNotEmpty(Object value) {
        return value != null && ! EMPTY.equals(value);
    }

    static
      public boolean isEmpty(Object value) {
        return value == null || EMPTY.equals(value);
    }
    
    /**
     * <p>Returns value of type. 
     * when data is {@code null}, returns default value.
     */
    @SuppressWarnings("unchecked")
	static
      public <T>T parse(String data, T defaultValue) {
    	if (data == null) return defaultValue;
		try {
			if (ClassUtils.isTypeOf(defaultValue.getClass(), String.class)) {
				return (T) data;
			} else if (ClassUtils.isTypeOf(defaultValue.getClass(), Integer.class)) {
    			return (T)Integer.valueOf(data);
			} else if (ClassUtils.isTypeOf(defaultValue.getClass(), Long.class)) {
				return (T)Long.valueOf(data);
			} else if (ClassUtils.isTypeOf(defaultValue.getClass(), Float.class)) {
				return (T)Float.valueOf(data);
			} else if (ClassUtils.isTypeOf(defaultValue.getClass(), Double.class)) {
				return (T)Double.valueOf(data);
    		}
		} catch (Exception e) {
		}
		return defaultValue;
    }
}
