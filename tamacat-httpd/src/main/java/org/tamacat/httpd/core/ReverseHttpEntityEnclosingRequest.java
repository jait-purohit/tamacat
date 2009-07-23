/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.Header;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.protocol.HTTP;
import org.tamacat.httpd.config.ReverseUrl;

/**
 * Client side request for reverse proxy.
 * (Implements HttpEntityEnclosingRequest)
 */
public class ReverseHttpEntityEnclosingRequest
		extends ReverseHttpRequest implements HttpEntityEnclosingRequest, Cloneable {

	private HttpEntity entity;
	
	public ReverseHttpEntityEnclosingRequest(HttpRequest request, ReverseUrl reverseUrl) {
		super(request, reverseUrl);
		if (request instanceof HttpEntityEnclosingRequest) {
			entity = ((HttpEntityEnclosingRequest)request).getEntity();
		}
	}

	@Override
    public HttpEntity getEntity() {
        return this.entity;
    }
	
	@Override
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public ReverseHttpEntityEnclosingRequest clone() throws CloneNotSupportedException {
    	ReverseHttpEntityEnclosingRequest clone = 
            (ReverseHttpEntityEnclosingRequest) super.clone();
        if (this.entity != null) {
            clone.entity = (HttpEntity) CloneUtils.clone(this.entity);
        }
        return clone;
    }
}
