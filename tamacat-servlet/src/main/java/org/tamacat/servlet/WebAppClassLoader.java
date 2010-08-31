package org.tamacat.servlet;

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

	public WebAppClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

//	@Override
//	public Class<?> loadClass(String name) throws ClassNotFoundException {
//		try {
//			return super.findClass(name);
//		} catch (Exception e) {
//			throw new ClassNotFoundException(e.getMessage(), e);
//		}
//	}
	
}
