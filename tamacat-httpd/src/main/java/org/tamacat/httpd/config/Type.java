/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

/**
 * <p>The type of service URL.
 */
public enum Type {
	
	NORMAL("normal"),
	REVERSE("reverse"),
	LB("lb"),
	ERROR("error");
	
	private final String name;
	Type(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Type find(String name) {
		return valueOf(name.toUpperCase());
	}
}
