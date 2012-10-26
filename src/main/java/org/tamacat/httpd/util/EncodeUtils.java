/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import org.tamacat.util.PropertyUtils;

public abstract class EncodeUtils {

	private static HashMap<String,String> charsetMap = new HashMap<String,String>();
	private static final String ENCODE_MAPPING_FILE = "encode-mapping.properties";
	
	static {
		try {
			Properties props = PropertyUtils.getProperties(
				"org/tamacat/httpd/util/" + ENCODE_MAPPING_FILE);
			addCharsetMap(props);
		} catch (Exception e) { //skip.
		}
		try {
			Properties props = PropertyUtils.getProperties(ENCODE_MAPPING_FILE);
			addCharsetMap(props);
		} catch (Exception e) { //skip.
		}
	}
	
	private static void addCharsetMap(Properties props) {
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (key != null && value != null) {
				charsetMap.put(key.toLowerCase().trim(), value.trim());
			}
		}
	}
	
	public static String getJavaEncoding(String charset) {
		return getJavaEncoding(charset, null);
	}
	
	public static String getJavaEncoding(String charset, String defaultCharset) {
		if (charset == null) return defaultCharset;
		String encoding = charsetMap.get(charset.toLowerCase());
		return encoding != null ? encoding : defaultCharset;
	}
}
