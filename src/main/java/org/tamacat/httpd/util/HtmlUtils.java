/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.tamacat.httpd.html.ConvertData;

public class HtmlUtils {
	
	public static final Pattern LINK_PATTERN = Pattern.compile(
			"<[^<]*\\s+(href|src|action|.*[0-9]*;?url)=(?:\'|\")?([^('|\")]*)(?:\'|\")?[^>]*>",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern CHARSET_PATTERN = Pattern.compile(
			"<meta[^<]*\\s+(content)=(.*);\\s?(charset)=(.*)['|\"][^>]*>",
			Pattern.CASE_INSENSITIVE);
	
	/**
	 * Get the character set from Content-type header.
	 * @param contentType
	 *    ex) key: "Content-Type", value: "text/html; charset=UTF-8"
	 * @return charset (lower case)
	 */
	public static String getCharSet(Header contentType) {
		if (contentType != null) {
			String value = contentType.getValue();
			if (value.indexOf("=") >= 0) {
				String[] values = value.split("=");
				if (values != null && values.length >= 2) {
					String charset = values[1];
					return charset.toLowerCase().trim();
				}
			}
		}
		return null;
	}

	/**
	 * Get the character set from HTML meta tag.
	 * @param html
	 * @param defaultCharset
	 * @return charset (lower case)
	 */
	public static String getCharSetFromMetaTag(String html, String defaultCharset) {
		if (html != null) {
			Matcher matcher = CHARSET_PATTERN.matcher(html);
			if (matcher.find()) {
				String charset = matcher.group(4);
				return charset != null ? charset.toLowerCase().trim()
						: defaultCharset;
			}
		}
		return defaultCharset;
	}
	
	public static ConvertData convertLink(String html, String before, String after) {
		return convertLink(html, before, after, LINK_PATTERN);
	}
	
	public static ConvertData convertLink(String html, String before, String after, Pattern pattern) {
		Matcher matcher = pattern.matcher(html);
		StringBuffer result = new StringBuffer();
		boolean converted = false;
		while (matcher.find()) {
			String url = matcher.group(2);
			if (url.startsWith("http://") || url.startsWith("https://")) {
				continue;
			}
			String rev = matcher.group().replaceFirst(before, after);
			matcher.appendReplacement(result, rev.replace("$", "\\$"));
			converted = true;
		}
		matcher.appendTail(result);
		return new ConvertData(result.toString(), converted);
	}
}
