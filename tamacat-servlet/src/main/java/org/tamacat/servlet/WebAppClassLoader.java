package org.tamacat.servlet;

import java.net.URL;
import java.net.URLClassLoader;

public class WebAppClassLoader extends URLClassLoader {

	public WebAppClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

}
