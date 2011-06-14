/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

public class IOUtils {


    public static InputStream getInputStream(String path) {
    	return getInputStream(path, ClassUtils.getDefaultClassLoader());
    }
    
    /**
     *<p>Get the InputStream.
     * @param path File path in the CLASSPATH
     * @return InputStream
     * @since 0.7
     */
    public static InputStream getInputStream(String path, ClassLoader loader) {
        URL url = ClassUtils.getURL(getClassPathToResourcePath(path), loader);
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
    
    public static String getClassPathToResourcePath(String path) {
        if (path == null || path.indexOf('/') >= 0) return path;
        int idx = path.lastIndexOf(".");
        if (idx >= 0) {
	        String name = path.substring(0, idx);
	        String ext = path.substring(idx, path.length());
	        return name.replace('.', '/') + ext;
        } else {
        	return path;
        }
    }
	static
	  public void close(Object target) {
		if (target != null) {
			if (target instanceof Closeable) {
				close((Closeable)target);
			} else {
				try {
					Method closable = ClassUtils.searchMethod(
							target.getClass(), "close");
					if (closable != null) closable.invoke(target);
				} catch (Exception e) {
				}
			}
		}
	}
	
	static
	  public void close(Closeable closable) {
	    try {
			if (closable != null) closable.close();
		} catch (IOException e) {
		}
	}
}
