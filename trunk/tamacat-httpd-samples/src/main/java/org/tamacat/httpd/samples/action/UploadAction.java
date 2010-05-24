/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.samples.action;

import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import org.tamacat.httpd.core.RequestContext;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.hdfs.util.HdfsFileUtils;

public class UploadAction {

	@SuppressWarnings("unchecked")
	public void upload(RequestContext request) {
		List<FileItem> list = (List<FileItem>) request.getAttribute(FileItem.class.getName());
		if (list != null) {
			try {
				for (FileItem item : list) {
					if (item.isFormField() == false) {
						HdfsFileUtils.write(item.getInputStream(), 
						item.getName());
					}
				}
			} catch (IOException e) {
				throw new ServiceUnavailableException(e);
			}
		}
	}
}
