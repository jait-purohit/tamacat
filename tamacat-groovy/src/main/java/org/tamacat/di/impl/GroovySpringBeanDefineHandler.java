/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import groovy.lang.GroovyClassLoader;

import org.tamacat.di.define.BeanDefine;
import org.tamacat.di.xml.SpringBeanDefineHandler;
import org.tamacat.groovy.ClasspathGroovyLoader;
import org.tamacat.groovy.GroovyLoader;
import org.tamacat.util.ClassUtils;
import org.xml.sax.Attributes;

public class GroovySpringBeanDefineHandler extends SpringBeanDefineHandler {
    
    protected GroovyLoader groovyLoader;

    public GroovySpringBeanDefineHandler() {
    }

    public GroovySpringBeanDefineHandler(GroovyLoader groovyLoader) {
    	this.groovyLoader = groovyLoader;
    }
    
    @Override
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    	groovyLoader = new ClasspathGroovyLoader(new GroovyClassLoader(loader));
    }
    
    protected GroovyLoader getGroovyLoader() {
    	if (groovyLoader == null) {
    		groovyLoader = new ClasspathGroovyLoader(new GroovyClassLoader(loader));
    	}
    	return groovyLoader;
    }

    @Override
    protected void startBean(Attributes attributes) {
        bean = new BeanDefine();
        bean.setId(attributes.getValue(ID));
        bean.setAliases(attributes.getValue(NAME));
        String type = attributes.getValue(CLASS);
        String groovy = attributes.getValue("type");
        if (groovy != null && "groovy".equalsIgnoreCase(groovy)) {
        	bean.setType(getGroovyLoader().loadClass(type));
        } else {
        	bean.setType(ClassUtils.forName(type, getClassLoader()));
        }
        //factory-method
        String factoryMethod = attributes.getValue(FACTORY_METHOD);
        bean.setFactoryMethod(factoryMethod);

        String scope = attributes.getValue(SCOPE);
        if (scope != null) {
            bean.setSingleton("singleton".equalsIgnoreCase(scope));
        } else {
        	String singleton = attributes.getValue(SINGLETON);
        	if (singleton != null) {
            	bean.setSingleton(Boolean.parseBoolean(singleton));
        	}
        }
        String initMethod = attributes.getValue(INIT_METHOD);
        if (initMethod != null && ! initMethod.equals("")) {
            bean.setInitMethod(initMethod);
        }
	}

}
