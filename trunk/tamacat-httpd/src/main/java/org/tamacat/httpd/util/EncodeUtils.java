/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import org.tamacat.util.PropertyUtils;

public class EncodeUtils {

	private static HashMap<String,String> map = new HashMap<String,String>();
	
	static {
		Properties props = PropertyUtils.getProperties(
				"org/tamacat/httpd/util/encode-mapping.properties");
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (key != null && value != null) {
				map.put(key.toLowerCase().trim(), value.trim());
			}
		}
	}
	
	public static String getJavaEncoding(String charset) {
		return getJavaEncoding(charset, null);
	}
	
	public static String getJavaEncoding(String charset, String defaultCharset) {
		if (charset == null) return defaultCharset;
		String encoding = map.get(charset.toLowerCase());
		return encoding != null ? encoding : defaultCharset;
	}
}
