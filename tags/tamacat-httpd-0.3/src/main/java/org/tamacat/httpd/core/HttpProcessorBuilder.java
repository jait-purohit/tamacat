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

public class HttpProcessorBuilder {

	private List<HttpRequestInterceptor> req = new ArrayList<HttpRequestInterceptor>();
	private List<HttpResponseInterceptor> res = new ArrayList<HttpResponseInterceptor>();
	
	public HttpProcessorBuilder addInterceptor(HttpRequestInterceptor interceptor) {
		req.add(interceptor);
		return this;
	}
	
	public HttpProcessorBuilder addInterceptor(HttpResponseInterceptor interceptor) {
		res.add(interceptor);
		return this;
	}
	
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
