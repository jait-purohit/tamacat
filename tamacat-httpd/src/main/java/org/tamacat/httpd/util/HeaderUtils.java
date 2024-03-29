/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.tamacat.util.StringUtils;

/**
 * <p>The utility class for HTTP request and response Headers.
 */
public final class HeaderUtils {

	/** Cannot instantiate. */
	HeaderUtils() {}

	/**
	 * <p>Get the first header value.
	 * @see {@link org.apache.http.HttpMessage#getFirstHeader}
	 * @param message
	 * @param name
	 * @return first header value.
	 */
	public static String getHeader(
			HttpMessage message, String name) {
		Header header = message.getFirstHeader(name);
		return header != null ? header.getValue() : null;
	}

	/**
	 * <p>Get the first header value.
	 * When header is null, returns default value.
	 * @see {@link org.apache.http.HttpMessage#getFirstHeader}
	 * @param message
	 * @param name
	 * @param defaultValue
	 * @return first header value.
	 */
	public static String getHeader(
			HttpMessage message, String name, String defaultValue) {
		Header header = message.getFirstHeader(name);
		return header != null ? header.getValue() : defaultValue;
	}

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
	public static List<Cookie> getCookies(String cookie) {
		List<Cookie> cookies = new ArrayList<Cookie>();
		if (StringUtils.isEmpty(cookie)) return cookies;
		StringTokenizer token = new StringTokenizer(cookie, ";");
		if (token != null) {
			while (token.hasMoreTokens()) {
				String line = token.nextToken();
				String[] nameValue = line.split("=");
				if (nameValue != null && nameValue.length > 0) {
					String key = nameValue[0].trim();
					String value = "";
					if (nameValue.length >= 2) {
						value = nameValue[1];
						if (value != null) {
							value = value.trim();
						}
					}
					Cookie c = new BasicClientCookie(key, value);
					cookies.add(c);
				}
			}
		}
		return cookies;
	}

	public static String getCookieValue(HttpRequest request, String name) {
		return getCookieValue(getHeader(request, "Cookie", ""), name);
	}

	/**
	 * <p>Get the Cookie value from Cookie header line.
	 * @param cookie header line.
	 * @param name Cookie name
	 * @return value of Cookie name in header line.
	 */
	public static String getCookieValue(String cookie, String name) {
		if (StringUtils.isEmpty(cookie)) return null;
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
							}
						}
						return "";
					}
				}
			}
		}
		return null;
	}

	/**
	 * <p>Check for use link convert.
	 * @param contentType
	 * @return true use link convert.
	 */
	public static boolean inContentType(Set<String> contentTypes, Header contentType) {
		if (contentType == null) return false;
		String type = contentType.getValue();
		if (contentTypes.contains(type)) {
			return true;
		} else {
			//Get the content sub type. (text/html; charset=UTF-8 -> html)
			String[] types = type != null ? type.split(";")[0].split("/") : new String[0];
			if (types.length >= 2 && contentTypes.contains(types[1])) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean isMultipart(String line) {
		return line != null && line.toLowerCase().startsWith("multipart/");
	}
}
