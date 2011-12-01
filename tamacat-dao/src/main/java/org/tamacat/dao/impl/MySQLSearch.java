/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import org.tamacat.dao.RdbSearch;

public class MySQLSearch extends RdbSearch {

    static class MySQLValueConvertFilter implements RdbSearch.ValueConvertFilter {
        public String convertValue(String value) {
            return value.replace("'", "''").replace("\\", "\\\\");
        }
    }

    public MySQLSearch() {
        super(new MySQLValueConvertFilter());
    }
}
