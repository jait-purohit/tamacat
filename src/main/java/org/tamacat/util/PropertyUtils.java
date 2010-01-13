/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public abstract class PropertyUtils {
	
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
