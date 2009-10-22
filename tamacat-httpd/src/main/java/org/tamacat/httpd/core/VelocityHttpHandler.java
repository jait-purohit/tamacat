/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityPage;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.util.FileUtils;

/**
 * <p>It is implements of {@link HttpHandler} that uses {@code Apache Velocity}. 
 */
public class VelocityHttpHandler extends AbstractHttpHandler {
	private VelocityPage page = new VelocityPage();

	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		String path = docsRoot + request.getRequestLine().getUri();
		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			path = requestParams[0];
//set request parameters for Custom HttpRequest.
//			if (requestParams.length >= 2) {
//				String params = requestParams[1];
//				String[] param = params.split("&");
//				for (String kv : param) {
//					String[] p = kv.split("=");
//					if (p.length >=2) {
//						//request.setParameter(p[0], p[1]);
//					}
//				}
//			}
		}
		int idx = path.lastIndexOf(".html");
		if (idx >= 0) {
			//delete the extention of file name. (index.html -> index)
			setEntity(request, response, path.replace(".html", ""));
		} else if (path.endsWith("/")) {
			//directory -> index page.
			setEntity(request, response, path + "index");
		} else {
			//get the file in this server.
			setFileEntity(request, response, path);
		}
	}
	
	private void setEntity(HttpRequest request, HttpResponse response, String path) {
		String html = page.getPage(request, response, path);
		ResponseUtils.setEntity(response, getEntity(html));
	}
	
	private void setFileEntity(HttpRequest request, HttpResponse response, String path) {
		try {
			URL r = FileUtils.getRelativePathToURL(getDecodeUri(path));
			if (r == null) throw new NotFoundException();
			File file = new File(r.toURI());
			if (file.exists() == false) throw new NotFoundException();
			ResponseUtils.setEntity(response, getFileEntity(file));
		} catch (URISyntaxException e) {
			throw new NotFoundException(e);
		}
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, getContentType(file));
        return body;
	}
}
