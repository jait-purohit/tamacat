/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.AbstractHttpHandler;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;

import com.bradmcevoy.http.ResourceFactory;

public class WebDavHttpHandler extends AbstractHttpHandler {

	private WebDavHttpManager manager;
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {	
		super.setServiceUrl(serviceUrl);
		ResourceFactory factory = new WebDavResourceFactory(serviceUrl, docsRoot);
		manager = new WebDavHttpManager(factory);
	}
	
	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		WebDavHttpRequest req = new WebDavHttpRequest(request, context);
		WebDavHttpResponse res = new WebDavHttpResponse(response, context);
		manager.process(req, res);
		response.removeHeaders(HTTP.CONTENT_LEN);
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			return new StringEntity(html);
		} catch (UnsupportedEncodingException e) {
			throw new NotFoundException(e);
		}
	}

	@Override
	protected HttpEntity getFileEntity(File file) {
		return new FileEntity(file, getContentType(file));
	}
}