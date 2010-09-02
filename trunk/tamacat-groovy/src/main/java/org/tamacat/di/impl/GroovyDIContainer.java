/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.tamacat.di.DIContainer;
import org.tamacat.di.define.BeanDefine;
import org.tamacat.di.define.BeanDefineMap;
import org.tamacat.groovy.ClasspathGroovyLoader;
import org.tamacat.groovy.GroovyLoader;
import org.tamacat.util.ClassUtils;

public class GroovyDIContainer implements DIContainer {
    
    private BeanDefineHandler beanDefineHandler;
    private GroovyBeanCreator creator;
    private GroovyLoader loader;

    public GroovyDIContainer(String xml) {
        init(xml, null);
    }

    public GroovyDIContainer(String xml, GroovyLoader loader) {
        init(xml, loader);
    }

    public GroovyDIContainer(BeanDefineMap defines, GroovyLoader loader) {
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
	

	GroovyLoader getClassLoader() {
        return loader;
    }

    private GroovyLoader loadClassLoader() {
        return new ClasspathGroovyLoader();
    }

    BeanDefineHandler loadBeanDefineHandler() {
        beanDefineHandler = new GroovySpringBeanDefineHandler(getClassLoader());
        return beanDefineHandler;
    }
    
    private void init(String xml, GroovyLoader loader) {
        this.loader = (loader != null) ? loader : loadClassLoader();
        this.creator = new GroovyBeanCreator(load(xml), getClassLoader());
    }
    
    private void init(BeanDefineMap defines, GroovyLoader loader) {
        this.loader = (loader != null) ? loader : loadClassLoader();
        this.creator = new GroovyBeanCreator(defines, getClassLoader());
    }
    
    private BeanDefineMap load(String xml){
        BeanDefineHandler handler = loadBeanDefineHandler();
        //handler.setClassLoader(getClassLoader());
        handler.setConfigurationFile(xml);
        return handler.getBeanDefines();
    }
}
