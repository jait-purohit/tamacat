/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.http.HttpRequest;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.core.RequestParameters;
import org.tamacat.httpd.mime.HttpFileUpload;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.StringUtils;

import com.bradmcevoy.http.AbstractRequest;
import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Cookie;
import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.RequestParseException;

public class WebDavHttpRequest extends AbstractRequest {

	private HttpRequest request;
	private HttpContext context;
	private Auth auth;
	
	public WebDavHttpRequest(HttpRequest request, HttpContext context) {
		this.request = request;
		this.context = context;
	}
	
	@Override
	public String getRequestHeader(Request.Header header) {
		return HeaderUtils.getHeader(request, header.name());
	}

	@Override
	public String getAbsoluteUrl() {
		return request.getRequestLine().getUri();
	}

	@Override
	public Auth getAuthorization() {
        if (auth == null) {
        	String h = getRequestHeader(Request.Header.AUTHORIZATION);
        	if (StringUtils.isNotEmpty(h)) {
        		auth = new Auth(h);
        	}
        }
        return auth;
	}

	@Override
	public Cookie getCookie(String name) {
		String value = HeaderUtils.getCookieValue(HeaderUtils.getHeader(request, "Cookie"),name);
		return new WebDavCookie(new BasicClientCookie(name, value));
	}

	@Override
	public List<Cookie> getCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		List<org.apache.http.cookie.Cookie> list = HeaderUtils.getCookies(HeaderUtils.getHeader(request, "Cookie"));
		for (org.apache.http.cookie.Cookie c : list) {
			cookies.add(new WebDavCookie(c));
		}
		return cookies;
	}

	@Override
	public String getFromAddress() {
		return RequestUtils.getRemoteIPAddress(context);
	}

	@Override
	public Map<String, String> getHeaders() {
		org.apache.http.Header[] headers = request.getAllHeaders();
		Map<String,String> map = new LinkedHashMap<String, String>();
		for (org.apache.http.Header h : headers) {
			map.put(h.getName(), h.getValue());
		}
		return map;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return RequestUtils.getInputStream(request);
	}

	@Override
	public Method getMethod() {
		String method = request.getRequestLine().getMethod();
		return Request.Method.valueOf(method);
	}

	@Override
	public void parseRequestParameters(Map<String, String> params,
			Map<String, FileItem> files) throws RequestParseException {
		if (RequestUtils.isMultipart(request)) {
			HttpFileUpload upload = new HttpFileUpload();
			try {
				List<org.apache.commons.fileupload.FileItem> list
					= upload.parseRequest(request);
				for (org.apache.commons.fileupload.FileItem item : list) {
					if (item.isFormField()) {
						params.put(item.getFieldName(), item.getString());
					} else {
						files.put(item.getFieldName(), new WebDavFileItem(item));
					}
				}
			} catch (FileUploadException e) {
				throw new RequestParseException(e.getMessage(), e);
			}
		} else {
			RequestParameters p = RequestUtils.getParameters(context);
			for (String name : p.getParameterNames()) {
				String[] values = p.getParameters(name);
				if (values != null && values.length > 0) {
					params.put(name, values[0]);
				}
			}
		}
	}

	@Override
	public void setAuthorization(Auth auth) {
		
	}

	@Override
	public String getRemoteAddr() {
		return RequestUtils.getRemoteIPAddress(context);
	}
}
