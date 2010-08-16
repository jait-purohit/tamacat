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
    
    private ClassLoader loader;
    
    //Load default DIContainer Class.
    public DIContainerFactory(final ClassLoader loader) {
    	this.loader = loader;
        try {
        	if (this.loader == null) {
        		this.loader = ClassUtils.getDefaultClassLoader();
        	}
            String className
                = PropertyUtils.getProperties(PROPERTIES_FILE, this.loader)
                	.getProperty("DIContainerClass");
            defaultDIContainerClass = ClassUtils.forName(className, this.loader);
        } catch (Exception e) {
            defaultDIContainerClass = TamaCatDIContainer.class;
        }
    }

    public synchronized DIContainer getInstance(String file) {
        DIContainer di = manager.get(file);
        if (di == null) {
        	Object[] args = {file, loader};
            di = (DIContainer) ClassUtils.newInstance(defaultDIContainerClass, args);
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
