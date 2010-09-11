/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketValidator {

	static final char LF = '\n'; //0x0a;
	static final char CR = '\r'; //0x0d;
	static final char NON_ASCII = 0x7f;
	static final Pattern NORMAL = Pattern.compile("^\\w");

	static final RuntimeException SYNTAX_ERR = new IllegalArgumentException();
	
	public static void validate(String data) {
		if (data != null && data.length() > 0) {
			if (data.indexOf(CR) >= 0
			 || data.indexOf(LF) >= 0) throw SYNTAX_ERR;
			Matcher matcher = NORMAL.matcher(data);
			if (matcher.find()) {
				throw SYNTAX_ERR;
			}
		}
	}
}
