/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.HashMap;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpRequestHandlerMapper} for a virtual host.<br>
 * With this HttpRequestHandlerMapper, I acquire virtual host setting based on
 * a Host request header and return a supporting {@link HttpRequestHandler}.
 */
public class HostRequestHandlerMapper {
	static final Log LOG = LogFactory.getLog(HostRequestHandlerMapper.class);

	/** default key for empty host.*/
	static final String DEFAULT_HOST = "default";

	private HashMap<String, HttpRequestHandlerMapper> hostHandler
		= new HashMap<String, HttpRequestHandlerMapper>();

	private boolean useVirtualHost = false;

	/**
	 * <p>Set the Host and {@link HttpRequestHandlerMapper}.
	 * @param host parameter is null then set the default {@link HttpRequestHandlerMapper}.
	 * @param mapper
	 */
	public void setHostRequestHandlerMapper(String host, HttpRequestHandlerMapper mapper) {
		if (host == null) {
			host = DEFAULT_HOST;
		}
		if (useVirtualHost == false && hostHandler.size() >= 1) {
			useVirtualHost = true;
		}
		if (host.equals(DEFAULT_HOST) == false) {
			LOG.info("add virtual host: " + host + "=" + mapper.getClass().getName());
		}
		hostHandler.put(host, mapper);
	}

	/**
	 * <p>Lookup the HttpRequestHandler for Host request header.
	 * @param request
	 * @param context
	 * @return HttpRequestHandler
	 */
	public HttpRequestHandler lookup(HttpRequest request, HttpContext context) {
		HttpRequestHandlerMapper mapper = null;
		if (useVirtualHost) {
			String host = RequestUtils.getRequestHostURL(request, context);
			if (host == null) {
				host = DEFAULT_HOST;
			}
			mapper = hostHandler.get(host);
		}
		if (mapper == null) {
			mapper = hostHandler.get(DEFAULT_HOST);
		}
		if (LOG.isTraceEnabled() && mapper != null) {
			LOG.trace("handler: " + mapper.getClass().getName());
		}
		HttpRequestHandler handler = null;
		if (mapper != null) {
			handler = mapper.lookup(request);
			//if (handler == null) {
			//	handler = mapper.lookup("/");
			//}
		}
		return handler;
	}
}
