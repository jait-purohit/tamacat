/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

public interface Search {

    void setStart(int start);
    void setMax(int max);
    int getStart();
    int getMax();

    String getSearchString();
}
