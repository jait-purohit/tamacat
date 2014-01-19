/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemHeadersSupport;

public class HttpFileItem implements FileItem, FileItemHeadersSupport {

	private static final long serialVersionUID = 1L;
	protected FileItem item;

	private String digest;

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public HttpFileItem(FileItem item) {
		this.item = item;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return item.getInputStream();
	}

	@Override
	public String getContentType() {
		return item.getContentType();
	}

	@Override
	public String getName() {
		return item.getName();
	}

	@Override
	public boolean isInMemory() {
		return item.isInMemory();
	}

	@Override
	public long getSize() {
		return item.getSize();
	}

	@Override
	public byte[] get() {
		return item.get();
	}

	@Override
	public String getString(String encoding)
			throws UnsupportedEncodingException {
		return item.getString(encoding);
	}

	@Override
	public String getString() {
		return item.getString();
	}

	@Override
	public void write(File file) throws Exception {
		item.write(file);
	}

	@Override
	public void delete() {
		item.delete();
	}

	@Override
	public String getFieldName() {
		return item.getFieldName();
	}

	@Override
	public void setFieldName(String name) {
		item.setFieldName(name);
	}

	@Override
	public boolean isFormField() {
		return item.isFormField();
	}

	@Override
	public void setFormField(boolean state) {
		item.setFormField(state);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return item.getOutputStream();
	}

	@Override
	public FileItemHeaders getHeaders() {
		if (item instanceof FileItemHeadersSupport) {
			return ((FileItemHeadersSupport)item).getHeaders();
		} else {
			return null;
		}
	}

	@Override
	public void setHeaders(FileItemHeaders headers) {
		if (item instanceof FileItemHeadersSupport) {
			((FileItemHeadersSupport)item).setHeaders(headers);
		}
	}
}
