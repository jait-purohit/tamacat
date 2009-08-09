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
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.action.ActionContext;
import org.tamacat.httpd.action.ActionHandler;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityPage;
import org.tamacat.util.ClassUtils;

/**
 * <p>It is implements of HttpHandler that uses {@code Apache Velociry}. 
 */
public class VelocityHttpHandler extends AbstractHttpHandler {

	private ActionHandler actionHandler;
	
	public void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}
	
	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		String path = docsRoot + request.getRequestLine().getUri();
		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			path = requestParams[0];
			if (requestParams.length >= 2) {
				String params = requestParams[1];
				String[] param = params.split("&");
				for (String kv : param) {
					String[] p = kv.split("=");
					if (p.length >=2) {
						//request.setParameter(p[0], p[1]);
					}
				}
			}
		}
		int idx = path.lastIndexOf(".html");
		if (idx >= 0) {
			path = path.replace(".html", "");
		} else if (path.endsWith("/")) {
			path = path + "index";
		} else {
			try {
				URL r = ClassUtils.getURL(getDecodeUri(path));
				if (r == null) throw new NotFoundException();
				File file = new File(r.toURI());
				if (file.exists() == false) throw new NotFoundException();
				response.setEntity(getFileEntity(file));
				response.setHeader(response.getEntity().getContentType());
				response.setHeader("Content-Length",String.valueOf(response.getEntity().getContentLength()));
				response.setHeader(response.getEntity().getContentEncoding());
			} catch (URISyntaxException e) {
				throw new NotFoundException(e);
			}
			return;
		}
		VelocityPage page = new VelocityPage();
		VelocityContext vc = new VelocityContext();
		if (actionHandler != null) {
			ActionContext actionContext = actionHandler.handleAction(
					request, response, context);
			for (String name : actionContext.getAttributeNames()) {
				vc.put(name, actionContext.getAttribute(name));
			}
		}
		String html = page.getPage(request, response, vc, path);
		response.setEntity(getEntity(html));
		response.setHeader("Content-Length",String.valueOf(response.getEntity().getContentLength()));
		response.setHeader(response.getEntity().getContentEncoding());
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
