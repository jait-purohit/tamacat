/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.HashMap;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpRequestHandlerResolver} for a virtual host.<br>
 * With this HandlerResolver, I acquire virtual host setting based on 
 * a Host request header and return a supporting {@link HttpRequestHandler}.
 */
public class HostRequestHandlerResolver {
	static final Log LOG = LogFactory.getLog(HostRequestHandlerResolver.class);

	/** default key for empty host.*/
	static final String DEFAULT_HOST = "default";
	
	private HashMap<String, HttpRequestHandlerResolver> hostHandler
		= new HashMap<String, HttpRequestHandlerResolver>();
	
	private boolean useVirtualHost = false;
	
	/**
	 * <p>Set the Host and {@link HttpRequestHandlerResolver}.
	 * @param host parameter is null then set the default {@link HttpRequestHandlerResolver}.
	 * @param resolver
	 */
	public void setHostRequestHandlerResolver(String host, HttpRequestHandlerResolver resolver) {
		if (host == null) {
			host = DEFAULT_HOST;
		}
		if (useVirtualHost == false && hostHandler.size() >= 1) {
			useVirtualHost = true;
		}
		if (host.equals(DEFAULT_HOST) == false) {
			LOG.info("add virtual host: " + host + "=" + resolver.getClass().getName());
		}
		hostHandler.put(host, resolver);
	}
	
	/**
	 * <p>Lookup the HttpRequestHandler for Host request header.
	 * @param request
	 * @param context
	 * @return HttpRequestHandler
	 */
	public HttpRequestHandler lookup(HttpRequest request, HttpContext context) {
		HttpRequestHandlerResolver resolver = null;
		if (useVirtualHost) {
			String host = RequestUtils.getRequestHostURL(request, context);
			if (host == null) {
				host = DEFAULT_HOST;
			}
			resolver = hostHandler.get(host);
		}
		if (resolver == null) {
			resolver = hostHandler.get(DEFAULT_HOST);
		}
		if (LOG.isTraceEnabled() && resolver != null) {
			LOG.trace("handler: " + resolver.getClass().getName());
		}
		HttpRequestHandler handler = null;
		if (resolver != null) {
			handler = resolver.lookup(request.getRequestLine().getUri());
			if (handler == null) {
				handler = resolver.lookup("/");
			}
		}
		return handler;
	}
}
