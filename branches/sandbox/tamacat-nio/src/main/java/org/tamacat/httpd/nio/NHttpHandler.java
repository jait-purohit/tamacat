/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.filter.HttpFilter;

/**
 * <p>{@code NHttpHandler} is NIO request handling interface.
 */
public interface NHttpHandler extends NHttpRequestHandler {

	/**
	 * <p>Set the {@link ServiceUrl}.
	 * @param serviceUrl
	 */
	void setServiceUrl(ServiceUrl serviceUrl);
	
	/**
	 * <p>Set the {@code HttpFilter}.(Add HttpFilter)<br>
	 * and execute {@link HttpFilter#init()}.
	 * @param filter
	 */
	void setHttpFilter(HttpFilter filter);
}
