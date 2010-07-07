/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.net.URI;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpRequestBase;

@NotThreadSafe // HttpRequestBase is @NotThreadSafe
public class HttpMkcol extends HttpRequestBase {
	public final static String METHOD_NAME = "MKCOL";
	
    public HttpMkcol() {}

    public HttpMkcol(final URI uri) {
        setURI(uri);
    }
    
    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpMkcol(final String uri) {
        setURI(URI.create(uri));
    }
    
	@Override
	public String getMethod() {
		return METHOD_NAME;
	}
}
