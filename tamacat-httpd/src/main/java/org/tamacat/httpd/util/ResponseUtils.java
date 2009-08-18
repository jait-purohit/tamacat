/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 * <p>The utility class for HTTP response.
 */
public class ResponseUtils {

	/**
	 * <p>Set the HttpEntity to response.
	 * Set the Content-Type, Content-Encoding and Content-Length response headers.
	 * @param response
	 * @param entity
	 */
	public static void setEntity(HttpResponse response, HttpEntity entity) {
		response.setEntity(entity);
		response.setHeader(response.getEntity().getContentType());
		response.setHeader("Content-Length",String.valueOf(response.getEntity().getContentLength()));
		response.setHeader(response.getEntity().getContentEncoding());
	}
}
