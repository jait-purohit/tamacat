package org.tamacat.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class WebAppClassLoader extends URLClassLoader {

	static final ClassLoader DEFAULT_PARENT
		= Thread.currentThread().getContextClassLoader();

	static final ClassLoader STATIC_LOADER = 
		WebAppClassLoader.class.getClassLoader() != null?
				WebAppClassLoader.class.getClassLoader() : ClassLoader.getSystemClassLoader();	
	
	public WebAppClassLoader(String docsRoot, ClassLoader parent) {
		super(new URL[]{},
			parent != null? 
				parent : DEFAULT_PARENT != null?
					DEFAULT_PARENT : STATIC_LOADER);
		try {
			addURL(new URL("file:" + docsRoot + "/WEB-INF/classes/"));
			File lib = new File(docsRoot + "/WEB-INF/lib");
			File[] jars = lib.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".jar");
				}
			});
			if (jars != null) {
				for (File f : jars) {
					addURL(f.toURI().toURL());
				}
			}
		} catch (MalformedURLException e) {
		}
	}

	public WebAppClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	@Override
	public void addURL(URL url) {
		super.addURL(url);
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
