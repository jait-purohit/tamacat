/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.handler.AbstractHttpHandler;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

import io.milton.http.ResourceFactory;

public class WebDavHttpHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(WebDavHttpHandler.class);

	private WebDavHttpManager manager;

	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		super.setServiceUrl(serviceUrl);
		ResourceFactory factory = new WebDavResourceFactory(serviceUrl, docsRoot);
		manager = new WebDavHttpManager(factory);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) {
		LOG.info(request.getRequestLine());

		//RequestUtils.setParameters(request, context, encoding);
		try {
			for (RequestFilter filter : requestFilters) {
				filter.doFilter(request, response, context);
			}
			doRequest(request, response, context);
		} catch (Exception e) {
			handleException(request, response, e);
		} finally {
			for (ResponseFilter filter : responseFilters) {
				filter.afterResponse(request, response, context);
			}
		}
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
		return new FileEntity(file, ContentType.parse(getContentType(file)));
	}
}