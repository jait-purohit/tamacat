/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.util.HashMap;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.apache.http.nio.protocol.NHttpRequestHandlerResolver;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link NHttpRequestHandlerResolver} for a virtual host.<br>
 * With this HandlerResolver, I acquire virtual host setting based on 
 * a Host request header and return a supporting {@link NHttpRequestHandler}.
 */
public class HostRequestHandlerResolver {
	static final Log LOG = LogFactory.getLog(HostRequestHandlerResolver.class);

	/** default key for empty host.*/
	static final String DEFAULT_HOST = "default";
	
	private HashMap<String, NHttpRequestHandlerResolver> hostHandler
		= new HashMap<String, NHttpRequestHandlerResolver>();
	
	private boolean useVirtualHost = false;
	
	/**
	 * <p>Set the Host and {@link NHttpRequestHandlerResolver}.
	 * @param host parameter is null then set the default {@link NHttpRequestHandlerResolver}.
	 * @param resolver
	 */
	public void setHostRequestHandlerResolver(String host, NHttpRequestHandlerResolver resolver) {
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
	 * <p>Lookup the NHttpRequestHandler for Host request header.
	 * @param request
	 * @param context
	 * @return NHttpRequestHandler
	 */
	public NHttpRequestHandler lookup(HttpRequest request, HttpContext context) {
		NHttpRequestHandlerResolver resolver = null;
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
		return resolver != null ? 
			resolver.lookup(request.getRequestLine().getUri()) : null;
	}
}
