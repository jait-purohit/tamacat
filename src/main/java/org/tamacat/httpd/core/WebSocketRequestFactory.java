package org.tamacat.httpd.core;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

public class WebSocketRequestFactory extends DefaultHttpRequestFactory {
	
	public HttpRequest newHttpRequest(final RequestLine requestline)
			throws MethodNotSupportedException {
		return new BasicHttpEntityEnclosingRequest(requestline);
	}

	public HttpRequest newHttpRequest(final String method, final String uri)
			throws MethodNotSupportedException {
		return new BasicHttpEntityEnclosingRequest(method, uri);
	}
}
