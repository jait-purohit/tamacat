package org.tamacat.httpd.core;

import java.util.HashMap;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class HostRequestHandlerResolver {
	static final Log LOG = LogFactory.getLog(HostRequestHandlerResolver.class);

	static final String DEFAULT_HOST = "default";
	
	private HashMap<String, HttpRequestHandlerResolver> hostHandler
		= new HashMap<String, HttpRequestHandlerResolver>();
	
	private boolean useVirtualHost = false;
	
	public void setHostRequestHandlerResolver(String host, HttpRequestHandlerResolver resolver) {
		if (host == null) {
			host = DEFAULT_HOST;
		}
		if (useVirtualHost == false && hostHandler.size() >= 1) {
			useVirtualHost = true;
		}
		if (host.equals(DEFAULT_HOST) == false) {
			LOG.info("add virtual host: " + host + "=" + resolver.getClass());
		}
		hostHandler.put(host, resolver);
	}
	
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
			LOG.trace("handler: " + resolver.getClass());
		}
		return resolver != null ? 
			resolver.lookup(request.getRequestLine().getUri()) : null;
	}
}
