package org.tamacat.cifs;

import java.util.Date;
import java.util.HashMap;

import org.tamacat.util.DateUtils;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class CifsFile extends HashMap<String, String> implements Comparable<CifsFile> {

	private static final long serialVersionUID = 1L;
	
	private final SmbFile file;
	private String name;
	private boolean isDirectory;
	private long length;
	private long lastModified;
	
	public String getName() {
		return name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}
	
	public boolean isFile() {
		return ! isDirectory;
	}

	public String getLength() {
		if (isFile()) {
			return String.format("%1$,3d KB", length/1024);
		} else {
			return "-";
		}
	}
	
	public String getLastModified() {
		return DateUtils.getTime(new Date(lastModified), "yyyy-MM-dd HH:mm");
	}

	public CifsFile(SmbFile file) {
		this.file = file;
		try {
			this.length = file.length();
			this.name = file.getName();

			this.isDirectory = file.isDirectory();
			this.lastModified = file.lastModified();
			
			put("length", getLength());
			put("isDirectory", String.valueOf(isDirectory()));
			put("getName", file.getName()); //StringUtils.encode(file.getName(),"UTF-8"));
			put("lastModified", getLastModified());
		} catch (SmbException e) {
		}
	}
	
	public SmbFile getSmbFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isDirectory ? 1231 : 1237);
		result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
		result = prime * result + (int) (length ^ (length >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CifsFile other = (CifsFile) obj;
		if (isDirectory != other.isDirectory)
			return false;
		if (lastModified != other.lastModified)
			return false;
		if (length != other.length)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(CifsFile o) {
		if (this == o) return 0;
		if (this.isDirectory() && o.isFile()) {
			return -1;
		} else if (this.isFile() && o.isDirectory()) {
			return 1;
		} else {
			return this.name.compareTo(o.name);
		}
	}
}
