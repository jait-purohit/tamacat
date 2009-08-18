/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.StringTokenizer;

import org.apache.http.Header;

/**
 * <p>The utility class for HTTP request and response Headers.
 */
public final class HeaderUtils {

	/** Cannot instantiate. */
	private HeaderUtils() {}
	
	/**
	 * <p>when each other's header names are equal returns true.
	 * The header name does not distinguish a capital letter, a small letter. 
	 * @param target target header.
	 * @param other other one.
	 * @return true, header names are equals.
	 */
	public static boolean equalsName(Header target, Header other) {
		if (target == null || other == null) {
			return false;
		} else {
			return target.getName().equalsIgnoreCase(other.getName());
		}
	}
	
	/**
	 * <p>Get the Cookie value from Cookie header line.
	 * @param cookie header line.
	 * @param name Cookie name
	 * @return value of Cookie name in header line.
	 */
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
