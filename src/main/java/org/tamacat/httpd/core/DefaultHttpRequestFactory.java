package org.tamacat.httpd.core;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

public class DefaultHttpRequestFactory extends
		org.apache.http.impl.DefaultHttpRequestFactory {
	
	static final String[] RFC2616_COMMON_METHODS = {
        "GET"
    };

    static final String[] RFC2616_ENTITY_ENC_METHODS = {
        "POST",
        "PUT"
    };

    static final String[] RFC2616_SPECIAL_METHODS = {
        "HEAD",
        "OPTIONS",
        "DELETE",
        "TRACE",
        "CONNECT"
    };

    public DefaultHttpRequestFactory() {}

    static boolean isOneOf(final String[] methods, final String method) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    public HttpRequest newHttpRequest(final RequestLine requestline)
            throws MethodNotSupportedException {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        String method = requestline.getMethod();
        if (isOneOf(RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        } else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        } else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        } else {
            throw new MethodNotSupportedException(method +  " method not supported");
            //return new BasicHttpEntityEnclosingRequest(requestline);
        }
    }

    public HttpRequest newHttpRequest(final String method, final String uri)
            throws MethodNotSupportedException {
        if (isOneOf(RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        } else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        } else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        } else {
            //return new BasicHttpEntityEnclosingRequest(method, uri);
            throw new MethodNotSupportedException(method +  " method not supported");
        }
    }
}
