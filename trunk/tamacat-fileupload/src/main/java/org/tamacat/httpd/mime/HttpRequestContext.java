/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.mime;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.RequestContext;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HTTP;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.StringUtils;

public class HttpRequestContext implements RequestContext {

	HttpRequest request;

	public HttpRequestContext(HttpRequest request) {
		this.request = request;
	}

	public HttpRequest getHttpRequest() {
		return request;
	}

	@Override
	public String getCharacterEncoding() {
		Header h = request.getFirstHeader(HTTP.CONTENT_ENCODING);
		return h != null? h.getValue() : null;
	}

	@Override
	public int getContentLength() {
		Header h = request.getFirstHeader(HTTP.CONTENT_LEN);
		return h != null? StringUtils.parse(h.getValue(),0) : 0;
	}

	@Override
	public String getContentType() {
		Header h = request.getFirstHeader(HTTP.CONTENT_TYPE);
		return h != null? h.getValue() : null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return RequestUtils.getInputStream(request);
	}
}
