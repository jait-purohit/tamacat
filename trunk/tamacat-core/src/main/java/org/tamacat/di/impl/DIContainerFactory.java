/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import java.util.HashMap;

import org.tamacat.di.DIContainer;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.PropertyUtils;

public class DIContainerFactory {

    private static final String PROPERTIES_FILE = "org.tamacat.di.DIContainerFactory.properties";
    private static Class<?> defaultDIContainerClass;
    private static HashMap<String, DIContainer> manager = new HashMap<String, DIContainer>();
    
    //Load default DIContainer Class.
    public DIContainerFactory(ClassLoader loader) {
        try {
        	if (loader == null) {
        		loader = ClassUtils.getDefaultClassLoader();
        	}
            String className
                = PropertyUtils.getProperties(PROPERTIES_FILE, loader)
                	.getProperty("DIContainerClass");
            defaultDIContainerClass = ClassUtils.forName(className, loader);
        } catch (Exception e) {
        	e.printStackTrace();
            defaultDIContainerClass = TamaCatDIContainer.class;
        }
    }

    public synchronized DIContainer getInstance(String file) {
        DIContainer di = manager.get(file);
        if (di == null) {
            di = (DIContainer) ClassUtils.newInstance(defaultDIContainerClass, file);
            manager.put(file, di);
        }
        return di;
    }
    
//    public synchronized DIContainer getInstance(String file, ClassLoader loader) {
//        DIContainer di = manager.get(file);
//        if (di == null) {
//        	
//        	Class<?> diClass;
//        	try {
//        		String className
//                	= PropertyUtils.getProperties(PROPERTIES_FILE, loader).getProperty("DIContainerClass");
//				diClass = loader.loadClass(className);
//			} catch (ClassNotFoundException e) {
//				diClass = TamaCatDIContainer.class;
//			}
//			di = (DIContainer) ClassUtils.newInstance(diClass, file);
//            manager.put(file, di);
//        }
//        return di;
//    }
}
