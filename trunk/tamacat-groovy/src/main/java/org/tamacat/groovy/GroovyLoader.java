/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

public interface GroovyLoader {

	Class<?> loadClass(String className);
	
	void recompile(String className);
	
	void recompile();
}
