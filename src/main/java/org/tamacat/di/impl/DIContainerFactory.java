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
    static {
        try {
            String className
                = PropertyUtils.getProperties(PROPERTIES_FILE).getProperty("DIContainerClass");
            defaultDIContainerClass = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
        } catch (Exception e) {
            defaultDIContainerClass = TamaCatDIContainer.class;
        }
    }

    public static synchronized DIContainer getInstance(String file) {
        DIContainer di = manager.get(file);
        if (di == null) {
            di = (DIContainer) ClassUtils.newInstance(defaultDIContainerClass, file);
            manager.put(file, di);
        }
        return di;
    }
}
