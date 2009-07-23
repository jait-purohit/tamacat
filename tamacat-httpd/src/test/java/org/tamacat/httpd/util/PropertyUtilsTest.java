/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class PropertyUtilsTest {

    static final String path1 = "server.properties";
    static final String path2 = "not_found.properties";

    @Test
    public void testGetProperties() {
        Properties props1 = PropertyUtils.getProperties(path1);
        assertEquals("url-config.xml",
                props1.getProperty("url-config.file"));
    }

    @Test
    public void testGetPropertiesNotFound() {
        try {
            Properties props = PropertyUtils.getProperties(path2);
            System.out.println(props);
            fail();
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
        }
    }
}
