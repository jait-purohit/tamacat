package org.tamacat.dao.impl;

import org.tamacat.dao.RdbSearch;

public class OracleSearch extends RdbSearch {
	
    static class OracleValueConvertFilter implements RdbSearch.ValueConvertFilter {
        public String convertValue(String value) {
            return value.replace("'", "''");
        }
    }

    public OracleSearch() {
        super(new OracleValueConvertFilter());
    }
}
