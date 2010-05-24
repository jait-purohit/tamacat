package org.tamacat.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtils {
    static final char SEPARATOR = File.separator.charAt(0);

    public static String getFilePath(String name) {
    	return name != null ? name.replace(SEPARATOR, '/') : "";
    }
    
    public static URL getRelativePathToURL(String name) {
    	String dir = System.getProperty("user.dir");
    	return getRelativePathToURL(dir, name);
    }
    
    public static URL getRelativePathToURL(String root, String name) {
    	String dir = root != null ? root : "";
    	try {
    	    String file = getFilePath(name);
    	    if (file.length() > 0 && file.charAt(0) != '/') {
    			dir = dir != null ? dir.replace(SEPARATOR, '/') + '/' : "/";
    			if (dir.charAt(0) != '/') dir = "/" + dir;
    			file = dir + file;
    	    }
    	    return new URL("file", "", file);
    	} catch (MalformedURLException e) {
    	    return null;
    	}
    }
    
	public static void writeFile(InputStream in, String path) throws IOException {
		writeFile(in, new File(path));
	}
	
	public static void writeFile(InputStream in, File file) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			InputStream is = new BufferedInputStream(in);
			byte[] fbytes = new byte[8192];
			while ((is.read(fbytes)) >= 0) {
				out.write(fbytes);
			}
		} finally {
			IOUtils.close(out);
		}
	}
    
	public static String normalizeFileName(String fileName) {
		return fileName != null ? 
			fileName.replace("..", "").replace("//","/")
			.replace("\r","").replace("\n","") : null;
	}
}
