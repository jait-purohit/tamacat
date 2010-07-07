/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bradmcevoy.http.FileItem;

public class WebDavFileItem implements FileItem {

	private org.apache.commons.fileupload.FileItem item;
	
	WebDavFileItem(org.apache.commons.fileupload.FileItem item) {
		this.item = item;
	}
	
	@Override
	public String getContentType() {
		return item.getContentType();
	}

	@Override
	public String getFieldName() {
		return item.getFieldName();
	}

	@Override
	public InputStream getInputStream() {
		try {
			return item.getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return item.getName();
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			return item.getOutputStream();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public long getSize() {
		return item.getSize();
	}
}
