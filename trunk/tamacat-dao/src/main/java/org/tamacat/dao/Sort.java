/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.util.MappingUtils;

public class Sort {

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
        if (k instanceof Column) {
        	sort.append(MappingUtils.getColumnName((Column)k) + " " + o.toString());
        } else {
        	sort.append(k.toString() + " " + o.toString());
        }
        return this;
    }
}
