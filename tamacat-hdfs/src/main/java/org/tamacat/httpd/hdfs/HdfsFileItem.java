/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.hdfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;

public class HdfsFileItem implements FileItem {

	private static final long serialVersionUID = 8416241515875710359L;
	
	private final FileItem item;
	private String baseDirectory;
	
	public HdfsFileItem(FileItem item, String baseDirectory) {
		this.item = item;
		this.baseDirectory = baseDirectory;
	}
	
	@Override
	public void delete() {
		item.delete();
	}

	@Override
	public byte[] get() {
		return item.get();
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
	public InputStream getInputStream() throws IOException {
		return item.getInputStream();
	}

	@Override
	public String getName() {
		return baseDirectory + item.getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return item.getOutputStream();
	}

	@Override
	public long getSize() {
		return item.getSize();
	}

	@Override
	public String getString() {
		return item.getString();
	}

	@Override
	public String getString(String encoding)
			throws UnsupportedEncodingException {
		return item.getString(encoding);
	}

	@Override
	public boolean isFormField() {
		return item.isFormField();
	}

	@Override
	public boolean isInMemory() {
		return item.isInMemory();
	}

	@Override
	public void setFieldName(String name) {
		item.setFieldName(name);
	}

	@Override
	public void setFormField(boolean state) {
		item.setFormField(state);
	}

	@Override
	public void write(File file) throws Exception {
		item.write(file);
	}
}
