/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.StringTokenizer;

import org.apache.http.Header;

public final class HeaderUtils {

	private HeaderUtils() {}
	
	public static boolean equalsName(Header target, Header other) {
		if (target == null || other == null) {
			return false;
		} else {
			return target.getName().equalsIgnoreCase(other.getName());
		}
	}
	
	public static String getCookieValue(String cookie, String name) {
		StringTokenizer token = new StringTokenizer(cookie, ";");
		if (token != null) {
			while (token.hasMoreTokens()) {
				String line = token.nextToken();
				String[] nameValue = line.split("=");
				if (nameValue != null && nameValue.length > 0) {
					String key = nameValue[0].trim();
					if (name.equalsIgnoreCase(key)) {
						if (nameValue.length >= 2) {
							String value = nameValue[1];
							if (value != null) {
								return value.trim();
							} else {
								return "";
							}
						}
					}
				}
			}
		}
		return null;
	}
}
