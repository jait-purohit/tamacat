/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.hdfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.filter.MultipartHttpFilter;
import org.tamacat.httpd.hdfs.util.HdfsFileUtils;
import org.tamacat.util.FileUtils;

public class FileUploadHttpFilter extends MultipartHttpFilter {
	
	@SuppressWarnings("unchecked")
	protected void handleFileItem(HttpContext context, FileItem item) {
		List<FileItem> list = (List<FileItem>)context.getAttribute(FileItem.class.getName());
		if (list == null) {
			list = new ArrayList<FileItem>();
		}
		String uri = getBaseDirectory() + "/" + FileUtils.normalizeFileName(item.getName());
		list.add(new HdfsFileItem(item, uri));
		context.setAttribute(FileItem.class.getName(), list);
	}
	
	@Override
	protected void writeFile(FileItem item, String name) throws IOException {
		String uri = getBaseDirectory() + "/" + FileUtils.normalizeFileName(name);
		HdfsFileUtils.write(item.getInputStream(), uri);
	}
}
