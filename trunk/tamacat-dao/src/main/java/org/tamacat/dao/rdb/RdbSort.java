/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import org.tamacat.dao.Sort;
import org.tamacat.dao.rdb.util.MappingUtils;

public class RdbSort implements Sort {

    StringBuffer sort = new StringBuffer();

    public Sort asc(Object k) {
        return sort(k, Order.ASC);
    }

    public Sort desc(Object k) {
        return sort(k, Order.DESC);
    }

    public String getSortString() {
        return sort.toString();
    }

    public Sort sort(Object k, Order o) {
        if (sort.length() > 0) sort.append(",");
        if (k instanceof RdbColumnMetaData) {
        	sort.append(MappingUtils.getColumnName((RdbColumnMetaData)k) + " " + o.toString());
        } else {
        	sort.append(k.toString() + " " + o.toString());
        }
        return this;
    }
}
