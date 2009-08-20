/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class DateUtils {

    static final Locale currentLocale = Locale.getDefault(); //JAPANESE;

    public static String getTime(Date date, String pattern) {
    	return getTime(date, pattern, currentLocale);
    }
    
    public static String getTime(Date date, String pattern, Locale locale) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        return formatter.format(date);
    }
    
    public static String getTimestamp(String pattern) {
    	return getTime(new Date(), pattern, currentLocale);
    }
    
    public static String getTimestamp(String pattern, Locale locale) {
    	return getTime(new Date(), pattern, locale);
    }
}
