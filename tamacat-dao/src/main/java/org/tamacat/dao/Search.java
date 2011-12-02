/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import org.tamacat.dao.meta.ColumnMetaData;
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

    public Search and(ColumnMetaData column, Condition condition, String... values) {
        if (search.length() > 0) search.append(" and ");
        search.append(parser.value(column, condition, values));
        return this;
    }

    public Search or(ColumnMetaData column, Condition condition, String... values) {
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

    protected static class DefaultValueConvertFilter implements ValueConvertFilter {
        public String convertValue(String value) {
            return value.replace("'", "''");
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
