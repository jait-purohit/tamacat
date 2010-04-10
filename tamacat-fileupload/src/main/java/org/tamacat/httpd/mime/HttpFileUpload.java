/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.mime;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.HttpRequest;

public class HttpFileUpload extends FileUpload {
	
	@SuppressWarnings("unchecked")
	public List<FileItem> parseRequest(HttpRequest request)
		throws FileUploadException {
		RequestContext ctx = new HttpRequestContext(request);
    	if (getFileItemFactory() == null) {
    		setFileItemFactory(new DiskFileItemFactory());
    	}
		return super.parseRequest(ctx);
    }
}
