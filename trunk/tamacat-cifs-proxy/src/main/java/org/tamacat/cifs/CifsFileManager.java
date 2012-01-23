package org.tamacat.cifs;

import java.util.LinkedHashMap;

public class CifsFileManager {

	LinkedHashMap<String, CifsFile[]> cifsFiles = new LinkedHashMap<String, CifsFile[]>();
	
	public void add(String path, CifsFile[] files) {
		this.cifsFiles.put(path, files);
	}
	
	public String[] getAllPaths() {
		return cifsFiles.keySet().toArray(new String[cifsFiles.size()]);
	}
	
	public CifsFile[] getCifsFiles(String path) {
		return cifsFiles.get(path);
	}
}
