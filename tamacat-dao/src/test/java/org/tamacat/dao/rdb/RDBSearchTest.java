/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.rdb.Condition;
import org.tamacat.dao.rdb.RdbSearch.ValueConvertFilter;

import junit.framework.TestCase;

public class RDBSearchTest extends TestCase {

	DefaultRdbTableMetaData table1;
    DefaultRdbColumnMetaData column1;
    DefaultRdbColumnMetaData column2;
    RdbSearch search;

    @Before
    protected void setUp() throws Exception {
    	table1 = new DefaultRdbTableMetaData("test1");
        column1 = new DefaultRdbColumnMetaData()
        	.setColumnName("name").setType(RdbDataType.STRING);

        column2 = new DefaultRdbColumnMetaData()
        	.setColumnName("id").setType(RdbDataType.NUMERIC);
        table1.registerColumn(column1, column2);
        search = new RdbSearch();
    }

    static class TestValueConvertFilter implements RdbSearch.ValueConvertFilter {
        public String convertValue(String value) {
            return value.replace("'", "''").replace("\\", "\\\\");
        }
    }

    @Test
    public void testRdbSearchConstructor() {
        ValueConvertFilter filter = new TestValueConvertFilter();
        search = new RdbSearch(filter);
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
