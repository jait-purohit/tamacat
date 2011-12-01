/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import org.tamacat.dao.meta.RdbColumnMetaData;
import org.tamacat.dao.util.MappingUtils;

public class RdbSort {

    public enum Order {

        ASC("asc"),
        DESC("desc");

        private String name;

        private Order(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    StringBuffer sort = new StringBuffer();

    public RdbSort asc(Object k) {
        return sort(k, Order.ASC);
    }

    public RdbSort desc(Object k) {
        return sort(k, Order.DESC);
    }

    public String getSortString() {
        return sort.toString();
    }

    public RdbSort sort(Object k, Order o) {
        if (sort.length() > 0) sort.append(",");
        if (k instanceof RdbColumnMetaData) {
        	sort.append(MappingUtils.getColumnName((RdbColumnMetaData)k) + " " + o.toString());
        } else {
        	sort.append(k.toString() + " " + o.toString());
        }
        return this;
    }
}
