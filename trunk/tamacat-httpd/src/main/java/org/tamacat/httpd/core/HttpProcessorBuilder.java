/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpProcessor;

/**
 * <p>The builder class for {@link HttpProcessor}.<br>
 * The {@link #build} method execute a build {@code HttpProcessor} and returns.
 */
public class HttpProcessorBuilder {

	private List<HttpRequestInterceptor> req = new ArrayList<HttpRequestInterceptor>();
	private List<HttpResponseInterceptor> res = new ArrayList<HttpResponseInterceptor>();
	
	/**
	 * <p>Add the {@link HttpRequestInterceptor}.
	 * @param interceptor
	 * @return added the interceptor object.
	 */
	public HttpProcessorBuilder addInterceptor(HttpRequestInterceptor interceptor) {
		req.add(interceptor);
		return this;
	}
	
	/**
	 * <p>Add the {@link HttpResponseInterceptor}.
	 * @param interceptor
	 * @return added the interceptor object.
	 */
	public HttpProcessorBuilder addInterceptor(HttpResponseInterceptor interceptor) {
		res.add(interceptor);
		return this;
	}
	
	/**
	 * <p>Create a new {@code HttpProcessor} and returns.
	 * @return Implements of {@code HttpProcessor}.
	 */
	public HttpProcessor build() {
        BasicHttpProcessor proc = new BasicHttpProcessor();
        for (HttpRequestInterceptor interceptor : req) {
        	proc.addInterceptor(interceptor);
        }
        for (HttpResponseInterceptor interceptor : res) {
        	proc.addInterceptor(interceptor);
        }
        return proc;
	}
}
