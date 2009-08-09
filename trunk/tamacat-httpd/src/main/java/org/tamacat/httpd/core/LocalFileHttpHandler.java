/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class LocalFileHttpHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(LocalFileHttpHandler.class);
	
	@Override
	public void doRequest(HttpRequest request, HttpResponse response, HttpContext context) {
		String uri = request.getRequestLine().getUri();
		File file = new File("./htdocs", getDecodeUri(uri));
		///// 404 NOT FOUND /////
		if (!file.exists()) {
		    LOG.trace("File " + file.getPath() + " not found");
		    throw new NotFoundException();
		}
		///// 403 FORBIDDEN /////
		else if (!file.canRead() || file.isDirectory()) {
		    LOG.trace("Cannot read file " + file.getPath());
		    throw new ForbiddenException();
		}
		///// 200 OK /////
		else {
		    LOG.trace("File " + file.getPath() + " found");
		    response.setStatusCode(HttpStatus.SC_OK);
		    response.setEntity(getFileEntity(file));
		    response.setHeader("Content-Length",String.valueOf(response.getEntity().getContentLength()));
			response.setHeader(response.getEntity().getContentEncoding());
		    LOG.trace("Serving file " + file.getPath());
		}
	}

	@Override
	protected HttpEntity getEntity(String html) {
		StringEntity body = null;
		try {
			body = new StringEntity(html);
			body.setContentType(DEFAULT_CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
		}
        return body;
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, getContentType(file));
        return body;
	}
}
