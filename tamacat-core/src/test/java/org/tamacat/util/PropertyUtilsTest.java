/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class PropertyUtilsTest {

    static final String path1 = "org/tamacat/di/DIContainer.properties";
    static final String path2 = "org.tamacat.di.DIContainer.properties";
    static final String path3 = "org.tamacat.di.DIContainer";

    static final String path4 = "org/tamacat/di/not_found.properties";

//    @Test
//    public void testGetProperties() {
//        Properties props1 = PropertyUtils.getProperties(path1);
//        assertEquals("org.tamacat.di.impl.UnloadableClassLoader",
//                props1.getProperty("DIContainerClassLoader"));
//
//        Properties props2 = PropertyUtils.getProperties(path2);
//        assertEquals("org.tamacat.di.impl.UnloadableClassLoader",
//                props2.getProperty("DIContainerClassLoader"));
//
//        Properties props3 = PropertyUtils.getProperties(path3);
//        assertEquals("org.tamacat.di.impl.UnloadableClassLoader",
//                props3.getProperty("DIContainerClassLoader"));
//    }

    @Test
    public void testGetPropertiesNotFound() {
        try {
            Properties props4 = PropertyUtils.getProperties(path4);
            System.out.println(props4);
            fail();
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
        }
    }

}
