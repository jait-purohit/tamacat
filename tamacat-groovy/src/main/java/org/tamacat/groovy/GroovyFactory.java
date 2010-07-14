/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import org.tamacat.util.ClassUtils;

public class GroovyFactory {

	static final ClasspathGroovyLoader LOADER = new ClasspathGroovyLoader();
	
	public static GroovyLoader getGroovyLoader() {
		return LOADER;
	}
	
	public static Object newInstance(String className) {
		return ClassUtils.newInstance(LOADER.loadClass(className));
	}
}
