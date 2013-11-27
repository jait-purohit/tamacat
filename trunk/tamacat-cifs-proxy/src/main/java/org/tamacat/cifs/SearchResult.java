package org.tamacat.cifs;

import java.io.Serializable;
import java.util.Date;

import org.tamacat.util.DateUtils;
import org.tamacat.util.StringUtils;

public class SearchResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String url;
	String folder;
	String name;
	long length;
	String lastModified;
	
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
	
	String unit;
	public String unit() {
		return unit;
	}
	
	public long getLength() {
		return length;
	}
	
	public void setLength(String length) {
		if (length.indexOf(' ')>=0) {
			String[] sizeUnit = length.replace(",","").split(" ");
			this.unit = sizeUnit[1].trim();
			long size = StringUtils.parse(sizeUnit[0], 0L);
			switch (unit) {
				case "KB": this.length = size * 1024; break;
				case "MB": this.length = size * 1024 * 1024; break;
				case "GB": this.length = size * 1024 * 1024 * 1024; break;
				case "TB": this.length = size * 1024 * 1024 * 1024 * 1024; break;
			}
		}
	}
	
	public Date getLastModifiedDate() {
		return DateUtils.parse(getLastModified(), "yyyy-MM-dd HH:mm");
	}
	
	public String getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
}
