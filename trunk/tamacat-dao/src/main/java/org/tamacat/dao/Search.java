/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import org.tamacat.dao.meta.Column;
import org.tamacat.sql.SQLParser;

public class Search {

    protected StringBuffer search = new StringBuffer();
    protected ValueConvertFilter valueConvertFilter;

    private int start;
    private int max;

    SQLParser parser;

    public Search() {
        parser = new SQLParser(new DefaultValueConvertFilter());
    }

    public Search(ValueConvertFilter valueConvertFilter) {
        parser = new SQLParser(valueConvertFilter);
    }

    public Search and(Column column, Condition condition, String... values) {
        if (search.length() > 0) search.append(" and ");
        search.append(parser.value(column, condition, values));
        return this;
    }

    public Search or(Column column, Condition condition, String... values) {
        if (search.length() > 0) search.append(" or ");
        search.append(parser.value(column, condition, values));
        return this;
    }

    public String getSearchString() {
        return search.toString();
    }

    /**
     * Value Convert Filter Interface
     */
    public static interface ValueConvertFilter {
        String convertValue(String value);
    }

    public static class DefaultValueConvertFilter implements ValueConvertFilter {
        public String convertValue(String value) {
        	if (value != null) {
        		return value.replace("'", "''");
        	} else {
        		return value;
        	}
        }
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
