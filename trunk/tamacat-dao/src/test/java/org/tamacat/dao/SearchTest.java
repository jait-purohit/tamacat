/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.Condition;
import org.tamacat.dao.Search;
import org.tamacat.dao.Search.ValueConvertFilter;
import org.tamacat.dao.meta.DefaultColumnMetaData;
import org.tamacat.dao.meta.DefaultTableMetaData;
import org.tamacat.dao.meta.DataType;

import junit.framework.TestCase;

public class SearchTest extends TestCase {

	DefaultTableMetaData table1;
    DefaultColumnMetaData column1;
    DefaultColumnMetaData column2;
    Search search;

    @Before
    protected void setUp() throws Exception {
    	table1 = new DefaultTableMetaData("test1");
        column1 = new DefaultColumnMetaData()
        	.setColumnName("name").setType(DataType.STRING);

        column2 = new DefaultColumnMetaData()
        	.setColumnName("id").setType(DataType.NUMERIC);
        table1.registerColumn(column1, column2);
        search = new Search();
    }

    static class TestValueConvertFilter implements Search.ValueConvertFilter {
        public String convertValue(String value) {
            return value.replace("'", "''").replace("\\", "\\\\");
        }
    }

    @Test
    public void testRdbSearchConstructor() {
        ValueConvertFilter filter = new TestValueConvertFilter();
        search = new Search(filter);
        search.and(column1, Condition.LIKE_HEAD, "Tama\\Cat");
        assertEquals("test1.name like 'Tama\\\\Cat%'", search.getSearchString());
    }

    @Test
    public void testAnd() {
        search.and(column1, Condition.LIKE_HEAD, "TamaCat");
        assertEquals("test1.name like 'TamaCat%'", search.getSearchString());

        search.and(column2, Condition.EQUAL, "123");
        assertEquals("test1.name like 'TamaCat%' and test1.id=123", search.getSearchString());
    }

    @Test
    public void testGetSearchString() {
        assertEquals("", search.getSearchString());

        search.and(column1, Condition.EQUAL, "TamaCat");
        assertEquals("test1.name='TamaCat'", search.getSearchString());
    }

    //for debug.
    static public void assertEquals(String expected, String actual) {
        //System.out.println(actual);
        assertEquals(null, expected, actual);
    }
}
