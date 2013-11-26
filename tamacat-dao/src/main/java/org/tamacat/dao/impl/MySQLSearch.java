/*
 * Copyright (c) 2007, tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import org.tamacat.dao.Search;

public class MySQLSearch extends Search {

    static class MySQLValueConvertFilter implements Search.ValueConvertFilter {
        public String convertValue(String value) {
            return value.replace("'", "''").replace("\\", "\\\\");
        }
    }

    public MySQLSearch() {
        super(new MySQLValueConvertFilter());
    }
}
