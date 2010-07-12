/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.tamacat.util.ClassUtils;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.ResourceNotFoundException;
import org.tamacat.util.StringUtils;

public class ClasspathGroovyLoader {
	
	static final String GROOVY_LOADER_CONFIG = "groovyloader.properties";
	static final String CLASSLOADER_CONFIG = "org.tamacat.groovy.groovyloader.properties";
	
	static final ClasspathGroovyLoader SELF = new ClasspathGroovyLoader();
	private GroovyClassLoader LOADER = new GroovyClassLoader();
	
	private long checkInterval = 10000; //10sec
	
	private GroovyClassCache cache;
	
	private ClasspathGroovyLoader() {
		Properties props = null;
		int maxClasses = 1000;
		long cacheExpireTime = 3600000; //60min
		try {
			props = PropertyUtils.getProperties(GROOVY_LOADER_CONFIG);
		} catch (ResourceNotFoundException e) {
			props = PropertyUtils.getProperties("org.tamacat.groovy." + GROOVY_LOADER_CONFIG);
		}
		if (props != null) {
			checkInterval = StringUtils.parse(props.getProperty("check_interval"),checkInterval);
			maxClasses = StringUtils.parse(props.getProperty("max_classes"),maxClasses);
			cacheExpireTime = StringUtils.parse(props.getProperty("cache_expire_time"),cacheExpireTime);
		}
		cache = new GroovyClassCache(maxClasses);
		cache.setCacheExpireTime(cacheExpireTime);
	}
	
	public static ClasspathGroovyLoader getInstance() {
		return SELF;
	}
    
	public synchronized Object getGroovy(final String name) {
		try {
			String fileName = getFileName(name);
			GroovyFile groovyFile = null;//cache.get(fileName);
			
			Class<?> c = null;
			if (groovyFile == null) {
				c = loadClass(fileName);
			} else {
				if (checkUpdate(groovyFile)) {
					c = loadClass(fileName);
				} else {
					c = groovyFile.getType();
				}
			}
			return c.newInstance();
		} catch (Exception e) {
			throw new GroovyClassLoaderException(e);
		}
	}
	
	public synchronized void recompile() {
		Set<String> keys = cache.keySet();
		for (String key : keys) {
			GroovyFile file = cache.get(key);
			Class<?> type = file.getType();
			recompile(type.getName());
		}
	}
	
	public synchronized void recompile(String className) {
		try {
			String fileName = className.replace(".","/") + ".groovy";
			loadClass(fileName);
		} catch (Exception e) {
			throw new GroovyClassLoaderException(e);
		}
	}
	
	public void clearCache() {
		cache.clear();
	}
	
	private Class<?> loadClass(String fileName) throws Exception {
		URL url = ClassUtils.getURL(fileName);
		File file = getGroovyFile(url);
		Class<?> c = LOADER.parseClass(file);
		if (c != null) {
			long lastModified = file.lastModified();
			cache.put(fileName, new GroovyFile(c, lastModified));
		}
		return c;
	}
	
	private File getGroovyFile(URL url) throws URISyntaxException {
		LOADER.clearCache();
		LOADER.addURL(url);
		File file = new File(url.toURI());
		return file;
	}
	
	private boolean checkUpdate(GroovyFile f) {
		return System.currentTimeMillis() - f.getCreateTime() < checkInterval;
	}
	
	private String getFileName(String name) {
		String fileName = name.lastIndexOf(".groovy")>=0? name : name + ".groovy";
		fileName = fileName.replaceFirst("^/", "");
		return fileName;
	}
}
