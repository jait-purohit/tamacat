/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.tamacat.httpd.config.ServiceUrl;

/**
 * 
 * <p>The factory interface of HttpHandler.
 */
public interface HttpHandlerFactory {

	/**
	 * <p>Returns the {@code HttpHandler}.
	 * @param serviceUrl
	 * @param handlerName
	 * @return HttpHandler
	 */
	HttpHandler getHttpHandler(ServiceUrl serviceUrl, String handlerName);
}
