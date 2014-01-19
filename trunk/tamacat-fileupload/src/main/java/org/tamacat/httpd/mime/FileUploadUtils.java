/*
 * Copyright (c) 2010 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.mime;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.filter.HttpFilter;

public class FileUploadUtils {

	@SuppressWarnings("unchecked")
	public static List<HttpFileItem> getFileItemList(HttpContext context) {
		return (List<HttpFileItem>) context.getAttribute(FileItem.class.getName());
	}

	public static Exception getException(HttpContext context) {
		return (Exception) context.getAttribute(HttpFilter.EXCEPTION_KEY);
	}
}
