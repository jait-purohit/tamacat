package org.tamacat.cifs;

public class SearchResult {

	String url;
	String folder;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	String name;
	String length;
	String lastModified;
	
}
