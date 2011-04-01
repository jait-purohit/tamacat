/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DateUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetDateTime() {
        System.out.println(DateUtils.getTimestamp("yyyy-MM-dd HH:mm:ss,S"));
        assertTrue(true);
    }

    @Test
    public void testParseTime() {
        System.out.println(DateUtils.parse("2011-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        assertTrue(true);
    }

}
