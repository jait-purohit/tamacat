package org.tamacat.dao.rdb.support;

import org.tamacat.dao.rdb.RdbSearch;

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
