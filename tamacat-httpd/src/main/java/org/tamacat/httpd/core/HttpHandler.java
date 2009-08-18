/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.protocol.HttpRequestHandler;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.filter.RequestFilter;

/**
 * <p>{@code HttpHandler} is request handling interface.
 */
public interface HttpHandler extends HttpRequestHandler {

	/**
	 * <p>Set the {@link ServiceUrl}.
	 * @param serviceUrl
	 */
	void setServiceUrl(ServiceUrl serviceUrl);
	
	/**
	 * <p>Set the {@code RequestFilter}.(Add RequestFilter)<br>
	 * and execute {@link RequestFilter#init()}.
	 * @param filter
	 */
	void setRequestFilter(RequestFilter filter);
}