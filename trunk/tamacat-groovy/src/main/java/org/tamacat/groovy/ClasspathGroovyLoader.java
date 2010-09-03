/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.codehaus.groovy.control.CompilationFailedException;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.ResourceNotFoundException;
import org.tamacat.util.StringUtils;

/**
 * <p>GroovyClassLoader find the Groovy script in CLASSPATH.
 */
public class ClasspathGroovyLoader implements GroovyLoader {
	
	static final String GROOVY_LOADER_CONFIG = "groovyloader.properties";
	static final String CLASSLOADER_CONFIG = "org.tamacat.groovy.groovyloader.properties";
	
	private GroovyClassLoader loader; //delegate ClassLoader
	
	private long checkInterval = 10000; //10sec
	
	private GroovyClassCache cache; //internal class cache
	
	/**
	 * <p.default constructor
	 */
	public ClasspathGroovyLoader() {
		this(new GroovyClassLoader());
	}
	
	/**
	 * <p>Constructor for custom GroovyClassLoader uses.
	 * @param loader GroovyClassLoader
	 */
	public ClasspathGroovyLoader(GroovyClassLoader loader) {
		this.loader = loader;
		Properties props = null;
		int maxClasses = 1000; //default
		long cacheExpireTime = 3600000; //default 60min
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

    /**
     * <p>Loads a class from a file path or class name(FQCN).
     *
     * @throws CompilationFailedException if compilation was not successful
     */
	public synchronized Class<?> loadClass(final String name) {
		try {
			String fileName = getFileName(name);
			GroovyFile groovyFile = cache.get(fileName);
			
			if (groovyFile == null) {
				groovyFile = parseClass(fileName);
			} else {
				if (checkUpdate(groovyFile)) {
					groovyFile = parseClass(fileName, groovyFile);
				}
			}
			if (groovyFile != null){
				cache.put(fileName, groovyFile);
				return groovyFile.getType();
			} else {
				//parent
				if (name.endsWith(".groovy")) return null;
				else return loader.getParent().loadClass(name);
			}
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
			String fileName = className.replace(".groovy","").replace(".","/") + ".groovy";
			parseClass(fileName);
		} catch (Exception e) {
			throw new GroovyClassLoaderException(e);
		}
	}
	
	public void clearCache() {
		cache.clear();
	}
	
    /**
     * <p>Parses the given file name a Java class capable of being run
     *
     * @param fileName (FQCN)
     * @param groovyFile cashed GroovyFile object.
     *        if groovyFile is null, then create a new GroovyFile instanse.
     * @return GroovyFile (Meta file)
     */
	private GroovyFile parseClass(String fileName, GroovyFile groovyFile) throws Exception {
		URL url = ClassUtils.getURL(fileName);
		if (url == null) return groovyFile;
		File file = getGroovyFile(url);
		long lastModified = file.lastModified();
		if (groovyFile == null || groovyFile.isUpdate(lastModified)) {
			GroovyCodeSource source = new GroovyCodeSource(file);
			source.setCachable(false); //cache disabled
			Class<?> c = loader.parseClass(source, false); //cache disabled
			if (c != null) {
				groovyFile = new GroovyFile(c, lastModified);
			}
		}
		return groovyFile;
	}
	
    /**
     * <p>Parses the given file name a Java class capable of being run
     *
     * @param fileName (FQCN)
     * @return GroovyFile (Meta file)
     */
	private GroovyFile parseClass(String fileName) throws Exception {
		return parseClass(fileName, null);
	}
	
	private File getGroovyFile(URL url) throws URISyntaxException {
		loader.addURL(url);
		File file = new File(url.toURI());
		return file;
	}
	
	private boolean checkUpdate(GroovyFile f) {
		return System.currentTimeMillis() - f.getCreateTime() > checkInterval;
	}
	
	private String getFileName(String name) {
		String fileName = name.replaceFirst("^/", "");
		if (name.endsWith(".groovy")) {
			fileName = fileName.replaceFirst(".groovy$", "").replace(".", "/") + ".groovy";
		}
		//String fileName = name.lastIndexOf(".groovy")>=0? name : name + ".groovy";
		return fileName;
	}
}
