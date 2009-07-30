/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;

import org.junit.Test;
import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testNewInstance() {
    }

    public void testForName() {
    }

    @Test
    public void testGetMethod() {
        Class<?> type = ServerConfig.class;
        Method m = ClassUtils.getMethod(type, "getParam", String.class);
        assertNotNull(m);
        assertEquals("getParam", m.getName());
    }


    @Test
    public void testGetStaticMethod() {
        Class<?> type = UniqueCodeGenerator.class;
        Method m = ClassUtils.getStaticMethod(type, "generate", (Class[]) null);
        assertNotNull(m);
        assertEquals("generate", m.getName());
    }

    public void testInvoke() {
    }

    @Test
    public void testGetSetterMethodName() {
        assertEquals("setCoreName", ClassUtils.getSetterMethodName("coreName"));
        assertEquals("setName", ClassUtils.getSetterMethodName("name"));
    }
    
    @Test
    public void testGetGetterMethodName() {
        assertEquals("getCoreName", ClassUtils.getGetterMethodName("coreName"));
        assertEquals("getName", ClassUtils.getGetterMethodName("name"));
    }
    
    @Test
    public void testGetSetterMethod() {
        assertEquals("setPath", ClassUtils.getSetterMethod("path", ServiceUrl.class).getName());
        assertEquals("setReverseUrl", ClassUtils.getSetterMethod("reverseUrl", ServiceUrl.class).getName());
    }
    
    @Test
    public void testGetGetterMethod() {
        assertEquals("getPath", ClassUtils.getGetterMethod("path", ServiceUrl.class).getName());
        assertEquals("getReverseUrl", ClassUtils.getGetterMethod("reverseUrl", ServiceUrl.class).getName());
    }
    
    @Test
    public void testIsType() {
    	assertTrue(ClassUtils.isTypeOf(String.class, Object.class));
    	assertTrue(ClassUtils.isTypeOf(ArrayList.class, List.class));
    	assertTrue(ClassUtils.isTypeOf(ArrayList.class, ArrayList.class));
    	assertTrue(ClassUtils.isTypeOf(HashMap.class, Serializable.class));
    	assertTrue(ClassUtils.isTypeOf(Integer.class,Number.class));
    	
    	assertFalse(ClassUtils.isTypeOf(List.class, ArrayList.class));
    	assertFalse(ClassUtils.isTypeOf(HashMap.class, List.class));
    }
    
    @Test
    public void testGetGenericType() {
    	Type[] types = ClassUtils.getGenericType(StringList.class);
    	for (Type type : types) {
    		//System.out.println(type);
        	assertEquals(
        		"java.util.ArrayList<java.lang.String>",
            	type.toString());
    	}

    }
    
    @Test
    public void testGetParameterizedType() {
    	ParameterizedType type = ClassUtils.getParameterizedType(StringList.class);
    	if(type != null) {
    		assertEquals(String.class, type.getActualTypeArguments()[0]);
    	}
    }
    
    @Test
    public void testGetParameterizedTypesClass() {
    	Type[] types = ClassUtils.getParameterizedTypes(StringList.class);
    	assertNotNull(types);
    	for (Type type : types) {
    		assertEquals(String.class, type);
    	}
    }
    
    @Test
    public void testGetParameterizedTypesE() {
    	Type[] types = ClassUtils.getParameterizedTypes(ArrayList.class);
    	assertNotNull(types);
    	for (Type type : types) {
    		assertEquals("E", type.toString());
    	}
    }
    
    @Test
    public void testGetParameterizedTypesNull() {
    	Type[] types = ClassUtils.getParameterizedTypes(String.class);
    	assertNull(types);
    }
    
    static class StringList extends ArrayList<String> {
		private static final long serialVersionUID = 1L;
	}

}
