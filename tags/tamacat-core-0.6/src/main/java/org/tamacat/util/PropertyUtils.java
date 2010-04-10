/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

/**
 * <p>The Utility of Properties.<br>
 * wheen file is not found then throws {@link ResourceNotFoundException}.
 */
public abstract class PropertyUtils {
	
	/**
	 * <p>Get the properties file, when file not found
	 *  then throws the {@link ResourceNotFoundException}.
	 * @param path File path in the CLASSPATH
	 * @return Properties file.
	 */
    public static Properties getProperties(String path) {
        Properties props = new Properties();
        InputStream in = null;
        try {
        	in = getInputStream(path);
            props.load(in);
        } catch (IOException e) {
            throw new ResourceNotFoundException(e);
        } finally {
            IOUtils.close(in);
        }
        return props;
    }

    /**
     *<p>Get the InputStream.
     * @param path File path in the CLASSPATH
     * @return InputStream
     */
    public static InputStream getInputStream(String path) {
        URL url = ClassUtils.getURL(getClassPathToResourcePath(path));
        InputStream in = null;
        try {
        	in = url.openStream();
        } catch (IOException e) {
            throw new ResourceNotFoundException(e);
        } catch (NullPointerException e) {
            throw new ResourceNotFoundException(path + " is not found.");
        }
        return in;
    }
    
    /**
     * <p>Marge the two property files.<br>
     * This method is not throws the {@link ResourceNotFoundException}.
     * When file not found, then always returns the empty properties.
     * @param defaultFile default properties.
     * @param addFile add or override the properties.
     * @return marged properties
     * @since 0.6
     */
    public static Properties marge(String defaultFile, String addFile) {
    	Properties props = null;
    	//Get the default properties.
    	try {
    		props = getProperties(defaultFile);
    	} catch (Exception e) {
    		props = new Properties(); //create empty properties.
    	}
    	//Override or add the properties.
    	try {
    		Properties add = getProperties(addFile);
    		Set<Object> keys = add.keySet();
    		for (Object key : keys) {
    			props.setProperty((String)key, add.getProperty((String)key));
    		}
    	} catch (Exception e) {
    		//none.
    	}
    	return props;
    }
    
    static String getClassPathToResourcePath(String path) {
        if (path == null || path.indexOf('/') >= 0) return path;
        int idx = path.lastIndexOf(".");
        if (idx > 0) {
	        String name = path.substring(0, idx);
	        String ext = path.substring(idx, path.length());
	        return name.replace('.', '/') + ext;
        } else {
        	return path;
        }
    }
}
