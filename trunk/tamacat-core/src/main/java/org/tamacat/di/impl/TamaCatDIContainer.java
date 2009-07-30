/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.tamacat.di.DIContainer;
import org.tamacat.di.define.BeanDefine;
import org.tamacat.di.define.BeanDefineMap;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.PropertyUtils;

public class TamaCatDIContainer implements DIContainer {

    static final String PROPERTIES_FILE = "org.tamacat.di.DIContainer.properties";
    static final String BEAN_DEFINE_HANDLER_KEY = "DIContainerBeanDefineHandler";
    
    private ClassLoader loader;
    private BeanDefineHandler beanDefineHandler;
    private BeanCreator creator;
    private Properties props;

    public TamaCatDIContainer(String xml) {
        init(xml, null);
    }

    public TamaCatDIContainer(String xml, ClassLoader loader) {
        init(xml, loader);
    }

    public TamaCatDIContainer(BeanDefineMap defines, ClassLoader loader) {
    	init(defines, loader);
    }

    @Override
    public Object getBean(String id) {
        return creator.getBean(id, null);
    }
    
    @Override
	public <T> T getBean(String id, Class<T> type) {
        return creator.getBean(id, type);
    }

    @Override
	public <T> List<T> getInstanceOfType(Class<T> type) {
        List<T> list = new ArrayList<T>();
        if (type == null) return list;
        for (Entry<String, BeanDefine> entry : creator.getDefines().entrySet()) {
            if (ClassUtils.isTypeOf(entry.getValue().getType(), type)) {
                list.add(getBean(entry.getValue().getId(), type));
            }
        }
        return list;
    }
    
    public void trace(PrintStream out) {
        out.println("-------------------------------------------");
        out.println("ClassLoader: " + getClassLoader().toString());
        for (Entry<String, BeanDefine> entry : creator.getDefines().entrySet()) {
            out.println(entry.getKey() + "=" + entry.getValue());
        }
        out.println("-------------------------------------------");
    }

    ClassLoader getClassLoader() {
        return loader;
    }

    void loadProperties() {
        props = PropertyUtils.getProperties(PROPERTIES_FILE);
    }

    ClassLoader loadClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }

    BeanDefineHandler loadBeanDefineHandler() {
        beanDefineHandler = (BeanDefineHandler) ClassUtils.newInstance(
            ClassUtils.forName(
                props.getProperty(BEAN_DEFINE_HANDLER_KEY),getClassLoader())
        );
        return beanDefineHandler;
    }
    
    private void init(String xml, ClassLoader loader) {
    	loadProperties();
        this.loader = (loader == null) ? loadClassLoader(): loader;
        this.creator = new BeanCreator(load(xml), getClassLoader());
    }
    
    private void init(BeanDefineMap defines, ClassLoader loader) {
    	loadProperties();
        this.loader = (loader == null) ? loadClassLoader(): loader;
        this.creator = new BeanCreator(defines, getClassLoader());
    }
    
    private BeanDefineMap load(String xml){
        BeanDefineHandler handler = loadBeanDefineHandler();
        handler.setClassLoader(getClassLoader());
        handler.setConfigurationFile(xml);
        return handler.getBeanDefines();
    }
}
