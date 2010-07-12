/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

public class GroovyClassLoaderCache_test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		for (;;) {
			Thread.sleep(5000);
			ClasspathGroovyLoader.getInstance().getGroovy("org/tamacat/groovy/test/Hello");
		}

	}

}
