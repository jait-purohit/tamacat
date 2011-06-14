/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

public class JavaVersion {

	public static final float JAVA_VERSION;
	static {
		JAVA_VERSION = StringUtils.parse(
			System.getProperty("java.specification.version"), 0f);
	}
	
	public static void main(String[] args) {
		System.out.println(JAVA_VERSION);
	}
}
