/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

public interface Sort {

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

    Sort asc(Object k);
    Sort desc(Object k);
    Sort sort(Object k, Order o);

    String getSortString();
}
