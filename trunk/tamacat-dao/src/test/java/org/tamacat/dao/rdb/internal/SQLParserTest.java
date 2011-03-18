/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.internal;

import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.rdb.Condition;
import org.tamacat.dao.rdb.DefaultRdbColumnMetaData;
import org.tamacat.dao.rdb.DefaultRdbTableMetaData;
import org.tamacat.dao.rdb.RdbDataType;
import org.tamacat.dao.rdb.RdbSearch.ValueConvertFilter;

import junit.framework.TestCase;

public class SQLParserTest extends TestCase {

	DefaultRdbTableMetaData table1;
	DefaultRdbColumnMetaData column1;
	DefaultRdbColumnMetaData column2;
	SQLParser parser;
	
	@Before
	protected void setUp() throws Exception {
		column1 = new DefaultRdbColumnMetaData()
			.setColumnName("name").setType(RdbDataType.STRING);
		
		column2 = new DefaultRdbColumnMetaData()
			.setColumnName("id").setType(RdbDataType.NUMERIC);
		
		table1 = new DefaultRdbTableMetaData("test1");
		table1.registerColumn(column1, column2);
		parser = new SQLParser();
	}

	static class TestValueConvertFilter implements ValueConvertFilter {
		public String convertValue(String value) {
			return value.replace("'", "''").replace("\\", "\\\\");
		}
	}
	
	@Test
	public void testParseValue() {
		assertEquals("'test'", parser.parseValue(column1, "test"));	
		assertEquals("123", parser.parseValue(column2, "123"));	
	}
	
	@Test
	public void testValue() {
		//System.out.println(parser.value(Condition.LIKE_PART, column1, "tama"));
		assertEquals("test1.name like '%tama%'", parser.value(column1, Condition.LIKE_PART, "tama"));
	}
	
	@Test
	public void testValues() {
		assertEquals("test1.name between 'a' and 'z'", parser.value(column1, Condition.BETWEEN, "a", "z"));
		assertEquals("test1.id between 100 and 200", parser.value(column2, Condition.BETWEEN, "100", "200"));
	}
	
	@Test
	public void testLikeStringValue() {
		assertEquals("test1.name like '%ta$_ma%' escape '$'",
				parser.value(column1, Condition.LIKE_PART, "ta_ma"));
		
		assertEquals("test1.name like '%$ta#_ma%' escape '#'",
				parser.value(column1, Condition.LIKE_PART, "$ta_ma"));
		
		assertEquals("test1.name like '%$%$_$%$_%' escape '$'",
				parser.value(column1, Condition.LIKE_PART, "%_%_"));
		
		assertEquals("test1.name like '%%'",
				parser.value(column1, Condition.LIKE_PART, ""));
	}
	
	@Test
	public void testInStringValue() {
		assertEquals("test1.name in ('abc','def','xyz')",
				parser.value(column1, Condition.IN, "abc", "def", "xyz"));
	}
	
	@Test
	public void testInIntValue() {
		assertEquals("test1.id in (123)",
				parser.value(column2, Condition.IN, "123"));
		assertEquals("test1.id in (123,456,789)",
				parser.value(column2, Condition.IN, "123", "456", "789"));
	}
	
	//for debug.
	static public void assertEquals(String expected, String actual) {
		//System.out.println(actual);
	    assertEquals(null, expected, actual);
	}
}
