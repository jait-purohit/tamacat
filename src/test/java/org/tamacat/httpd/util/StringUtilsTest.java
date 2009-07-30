/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testIsNotEmpty() {
    	assertTrue(StringUtils.isNotEmpty("TEST"));
    	assertTrue(StringUtils.isNotEmpty(new String("123")));
    	
    	assertFalse(StringUtils.isNotEmpty(null));
    	assertFalse(StringUtils.isNotEmpty(""));
    	assertFalse(StringUtils.isNotEmpty(new String()));
    	assertFalse(StringUtils.isNotEmpty(new String("")));
    }

    @Test
    public void testIsEmpty() {
    	assertFalse(StringUtils.isEmpty("TEST"));
    	assertFalse(StringUtils.isEmpty(new String("123")));
    	
    	assertTrue(StringUtils.isEmpty(null));
    	assertTrue(StringUtils.isEmpty(""));
    	assertTrue(StringUtils.isEmpty(new String()));
    	assertTrue(StringUtils.isEmpty(new String("")));
    }

    @Test
    public void testParse() {
        assertEquals(100, StringUtils.parse("100", 123));
        assertEquals(200L, StringUtils.parse("200", 123L));
        assertEquals(100.00f, StringUtils.parse("100.00", 123.456f));
        assertEquals(200.00d, StringUtils.parse("200.00", 123.456d));

        assertEquals(123, StringUtils.parse("TEST", 123));
        assertEquals(123L, StringUtils.parse("TEST", 123L));
        assertEquals(123.456f, StringUtils.parse("TEST", 123.456f));
        assertEquals(123.456d, StringUtils.parse("TEST", 123.456d));

        assertEquals(123, StringUtils.parse("", 123));
        assertEquals(123L, StringUtils.parse("", 123L));
        assertEquals(123.456f, StringUtils.parse("", 123.456f));
        assertEquals(123.456d, StringUtils.parse("", 123.456d));
        
        assertEquals(123, StringUtils.parse(null, 123));
        assertEquals(123L, StringUtils.parse(null, 123L));
        assertEquals(123.456f, StringUtils.parse(null, 123.456f));
        assertEquals(123.456d, StringUtils.parse(null, 123.456d));
        
        assertEquals(null, StringUtils.parse(null, null));
        assertEquals(null, StringUtils.parse("TEST", null));
    }
}
