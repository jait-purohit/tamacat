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

    public static String getTimestamp(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        return formatter.format(new Date());
    }
    
    public static String getTimestamp(String pattern, Locale locale) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        return formatter.format(new Date());
    }
}
