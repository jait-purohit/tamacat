/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import org.tamacat.util.ClassUtils;

import groovy.lang.GroovyClassLoader;

public class GroovyClassLoaderCache_test {

	public static void main(String[] args) throws Exception {
		ClasspathGroovyLoader loader = new ClasspathGroovyLoader();
		@SuppressWarnings("unused")
		GroovyClassLoader loader2 = new GroovyClassLoader();
		long start = System.currentTimeMillis();
		for (int i=0; i<100; i++) {
			//Thread.sleep(5000);
			Class<?> c = loader.loadClass("org/tamacat/groovy/test/Hello");
			System.out.println(ClassUtils.newInstance(c));
		}
		System.out.println(System.currentTimeMillis() - start);
	}
}
