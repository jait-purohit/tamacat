package org.tamacat.servlet.impl;

import java.net.URL;
import java.net.URLClassLoader;

public class WebAppClassLoader extends URLClassLoader {

	static final ClassLoader DEFAULT_PARENT
		= Thread.currentThread().getContextClassLoader();
	
	static final ClassLoader STATIC_LOADER = 
		WebAppClassLoader.class.getClassLoader() != null?
				WebAppClassLoader.class.getClassLoader() : ClassLoader.getSystemClassLoader();	
	
	public WebAppClassLoader(ClassLoader parent) {
		super(new URL[]{},
			parent != null? 
				parent : DEFAULT_PARENT != null?
					DEFAULT_PARENT : STATIC_LOADER);
	}
}
