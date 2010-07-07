/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.util.HeaderUtils;

import com.bradmcevoy.http.AbstractResponse;
import com.bradmcevoy.http.Cookie;
import com.bradmcevoy.http.Response;

public class WebDavHttpResponse extends AbstractResponse {

	private HttpResponse response;
	@SuppressWarnings("unused")
	private HttpContext context;
	private OutputStream out;
	
	public WebDavHttpResponse(HttpResponse response, HttpContext context) {
		this.response = response;
		this.context = context;
	}
	
	@Override
	public Map<String, String> getHeaders() {
		org.apache.http.Header[] headers = response.getAllHeaders();
		Map<String,String> map = new LinkedHashMap<String, String>();
		for (org.apache.http.Header h : headers) {
			map.put(h.getName(), h.getValue());
		}
		return map;
	}

	@Override
	public String getNonStandardHeader(String name) {
		return HeaderUtils.getHeader(response, name);
	}

	@Override
	public OutputStream getOutputStream() {
		if (out == null) {
			final ByteArrayOutputStream o = new ByteArrayOutputStream();
			ContentProducer producer = new ContentProducer() {
				public void writeTo(OutputStream out) throws IOException {
					out.write(o.toByteArray());
				}
			};
			HttpEntity entity = new EntityTemplate(producer);
			response.setEntity(entity);
			this.out = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					o.write((int)b);
				}
			};
		}
		return out;
	}

	@Override
	public Status getStatus() {
		return Status.valueOf(
			String.valueOf(response.getStatusLine().getStatusCode()));
	}

	@Override
	public void setAuthenticateHeader(List<String> challenges) {
		for (String ch : challenges) {
			response.addHeader(Response.Header.WWW_AUTHENTICATE.code, ch);
		}
	}

	@Override
	public Cookie setCookie(Cookie cookie) {
		response.addHeader("Set-Cookie", cookie.toString()); //TODO
		return cookie;
	}

	@Override
	public Cookie setCookie(String key, String value) {
		WebDavCookie cookie = new WebDavCookie(new BasicClientCookie(key, value));
		response.addHeader("Set-Cookie", cookie.toString()); //TODO
		return cookie;
	}

	@Override
	public void setNonStandardHeader(String name, String value) {
		response.setHeader(name, value);
	}

	@Override
	public void setStatus(Status status) {
		response.setStatusCode(status.code);
		response.setReasonPhrase(status.name());
	}
}
