/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.tamacat.core.Core;
import org.tamacat.core.CoreFactory;
import org.tamacat.core.SampleCore;

import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGetDefaultClassLoader() {
    	assertNotNull(ClassUtils.getDefaultClassLoader());
    	assertEquals(
    		Thread.currentThread().getContextClassLoader(),
    		ClassUtils.getDefaultClassLoader()
    	);
    }
    
    @Test
    public void testGetStream() {
    	assertNotNull(ClassUtils.getStream(""));
    }
    
    @Test
    public void testGetURL() {
    	assertNotNull(ClassUtils.getURL(""));
    }
    
    @Test
    public void testGetURLAndClassLoader() {
    	assertNotNull(ClassUtils.getURL("", Thread.currentThread().getContextClassLoader()));
    }
    
    @Test
    public void testNewInstance() {
    	assertEquals("", ClassUtils.newInstance(String.class));
    	assertEquals(null, ClassUtils.newInstance(ClassUtils.class));
    }

    public void testForName() {
    }

    @Test
    public void testGetMethod() {
        Class<?> type = SampleCore.class;
        Method m = ClassUtils.getMethod(type, "setCore", Core.class);
        assertNotNull(m);
        assertEquals("setCore", m.getName());
        
        Method m2 = ClassUtils.getMethod(type, "", Core.class);
        assertNull(m2);
    }


    @Test
    public void testGetStaticMethod() {
        Class<?> type = CoreFactory.class;
        Method m = ClassUtils.getStaticMethod(type, "createCore", (Class[]) null);
        assertNotNull(m);
        assertEquals("createCore", m.getName());
    }

    @Test
    public void testGetDeclaredMethod() {
        Class<?> type = SampleCore.class;
        Method m = ClassUtils.getDeclaredMethod(type, "setCore", Core.class);
        assertNotNull(m);
        assertEquals("setCore", m.getName());
        
        Method m2 = ClassUtils.getDeclaredMethod(type, "", Core.class);
        assertNull(m2);
    }
    
    public void testInvoke() {
    }

    @Test
    public void testGetAdderMethodName() {
        assertEquals("addCoreName", ClassUtils.getAdderMethodName("coreName"));
        assertEquals("addName", ClassUtils.getAdderMethodName("name"));
        
        assertEquals("add", ClassUtils.getAdderMethodName(""));
    }
    
    @Test
    public void testGetRemoveMethodName() {
        assertEquals("removeCoreName", ClassUtils.getRemoveMethodName("coreName"));
        assertEquals("removeName", ClassUtils.getRemoveMethodName("name"));
        
        assertEquals("remove", ClassUtils.getRemoveMethodName(""));
    }
    
    @Test
    public void testGetSetterMethodName() {
        assertEquals("setCoreName", ClassUtils.getSetterMethodName("coreName"));
        assertEquals("setName", ClassUtils.getSetterMethodName("name"));
        
        assertEquals("set", ClassUtils.getSetterMethodName(""));
    }
    
    @Test
    public void testGetGetterMethodName() {
        assertEquals("getCoreName", ClassUtils.getGetterMethodName("coreName"));
        assertEquals("getName", ClassUtils.getGetterMethodName("name"));
        
        assertEquals("get", ClassUtils.getGetterMethodName(""));
    }
    
    @Test
    public void testGetSetterMethod() {
        assertEquals("setCoreName", ClassUtils.getSetterMethod("coreName", SampleCore.class).getName());
        assertEquals("setName", ClassUtils.getSetterMethod("name", SampleCore.class).getName());
    }
    
    @Test
    public void testGetGetterMethod() {
        assertEquals("getCoreName", ClassUtils.getGetterMethod("coreName", SampleCore.class).getName());
        assertEquals("getName", ClassUtils.getGetterMethod("name", SampleCore.class).getName());
    }
    
    @Test
    public void testIsTypeOf() {
    	assertTrue(ClassUtils.isTypeOf(String.class, Object.class));
    	assertTrue(ClassUtils.isTypeOf(ArrayList.class, List.class));
    	assertTrue(ClassUtils.isTypeOf(ArrayList.class, ArrayList.class));
    	assertTrue(ClassUtils.isTypeOf(HashMap.class, Serializable.class));
    	assertTrue(ClassUtils.isTypeOf(Integer.class,Number.class));
    	
    	assertFalse(ClassUtils.isTypeOf(List.class, ArrayList.class));
    	assertFalse(ClassUtils.isTypeOf(HashMap.class, List.class));
    
    	assertFalse(ClassUtils.isTypeOf(null, List.class));
    	assertFalse(ClassUtils.isTypeOf(HashMap.class, null));
    	assertTrue(ClassUtils.isTypeOf(null, null));
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
    	assertEquals(0, ClassUtils.getGenericType(Object.class).length);
    }
    
    @Test
    public void testGetParameterizedType() {
    	ParameterizedType type = ClassUtils.getParameterizedType(StringList.class);
    	if(type != null) {
    		assertEquals(String.class, type.getActualTypeArguments()[0]);
    	}
    	assertNull(ClassUtils.getParameterizedType(null));
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
    
    static class StringMap extends HashMap<String,String> {
		private static final long serialVersionUID = 1L;
	}
}
