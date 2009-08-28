/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tamacat.util.DateUtils;

public class DynamicRealm {
	
	static final Pattern REALM_DATE_PATTERN = Pattern.compile("(.*\\$\\{)(.*)(\\}.*)");
	
	public static String getRealm(String realm) {
		Matcher matcher = REALM_DATE_PATTERN.matcher(realm);
		if (matcher.find()) {
		    String pattern = matcher.group(2);
		    if (pattern != null) {
		    	return realm.replace("${" + pattern + "}",
		    			DateUtils.getTime(new Date(), pattern));
		    }
		}
		return realm;
	}
}
