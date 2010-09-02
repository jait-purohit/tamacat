package org.tamacat.groovy.test;

public class Singleton {

	static final Singleton SELF = new Singleton();
	
	public Singleton getInstance() {
		return SELF;
	}
}
