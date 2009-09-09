/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tamacat.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public abstract class StringUtils {

    private static final String EMPTY = "";

    static
      public boolean isNotEmpty(Object value) {
        return value != null && ! EMPTY.equals(value);
    }

    static
      public boolean isEmpty(Object value) {
        return value == null || EMPTY.equals(value);
    }

    static
      public boolean exists(String value, String str) {
    	return value != null && value.indexOf(str) >=0;
    }
    
    static 
      public String toLowerCase(String value) {
    	return value != null ? value.toLowerCase() : value;
    }
    
    static 
      public String toUpperCase(String value) {
  	    return value != null ? value.toUpperCase() : value;
    }
    
    static 
      public String trim(String value) {
    	return value != null ? value.trim() : value;
    }
    
    static
      public String[] toStringArray(Collection<String> collection) {
        return collection != null ? collection.toArray(new String[collection.size()]) : null;
    }
    
    static
      public String getStackTrace(Throwable e) {
    	StackTraceElement[] elements = e.getStackTrace();
    	StringBuilder builder = new StringBuilder();
    	for (StackTraceElement element : elements) {
    		if (builder.length() > 0) {
    			builder.append("\t");
    		}
    		builder.append(element).append("\n");
    	}
    	return builder.toString();
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
    
    static
      public String decode(String str, String encoding) {
    	if (str == null || str.length() == 0) return str;
    	try {
			return new String(str.getBytes("iso-8859-1"), encoding);
		} catch (UnsupportedEncodingException e) {
			return str;
		}
    }

    static
      public String encode(String str, String encoding) {
  	    if (str == null || str.length() == 0) return str;
  	    try {
			return new String(str.getBytes(encoding), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
    }
    
    static
      public String dump(byte[] str) {
    	StringBuilder data = new StringBuilder();
    	for(byte b : str) {
    		String hex = Integer.toHexString((int)b);
    		data.append(hex); //System.out.print(hex);
    	}
    	return data.toString();
    }
}
