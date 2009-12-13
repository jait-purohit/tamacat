package org.tamacat.servlet.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

public class ServletUtils {

	public static String[] getHeaderValues(HttpRequest request, String name) {
		Header[] headers = request.getHeaders(name);
		String[] values = null;
		if (headers != null && headers.length > 0) {
			values = new String[headers.length];
			for (int i=0; i<headers.length; i++) {
				Header h = headers[i];
				values[i] = h.getValue();
			}
		}
		return values;
	}
	
	public static Cookie[] getCookies(HttpRequest request) {
		Header[] headers = request.getHeaders("Cookie");
		List<Cookie> cookies = new ArrayList<Cookie>();
		for (Header h : headers) {
			String cookie = h.getValue();
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
						Cookie c = new Cookie(key, value);
						cookies.add(c);
					}
				}
			}
		}
		return cookies.toArray(new Cookie[cookies.size()]);
	}
	
	public static long getTime(String value) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
			return df.parse(value).getTime();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static String getDate(long time) {
		SimpleDateFormat df = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		return df.format(new Date(time));
	}
}
