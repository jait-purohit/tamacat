package org.tamacat.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtils {
    static final char SEPARATOR = File.separator.charAt(0);

    public static String getFilePath(String name) {
    	return name !=  null ? name.replace(SEPARATOR, '/') : "";
    }
    
    public static URL getRelativePathToURL(String name) {
    	try {
    	    String file = getFilePath(name);
    	    if (file.length() > 0 && file.charAt(0) != '/') {
    	    	String dir = System.getProperty("user.dir");
    			dir = dir != null ? dir.replace(SEPARATOR, '/') + '/' : "/";
    			if (dir.charAt(0) != '/') dir = "/" + dir;
    			file = dir + file;
    	    }
    	    return new URL("file", "", file);
    	} catch (MalformedURLException e) {
    	    return null;
    	}
    }
}
