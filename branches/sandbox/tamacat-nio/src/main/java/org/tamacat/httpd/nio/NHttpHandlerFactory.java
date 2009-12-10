/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.tamacat.httpd.config.ServiceUrl;

/**
 * 
 * <p>The factory interface of NHttpRequestHandler.
 */
public interface NHttpHandlerFactory {

	/**
	 * <p>Returns the {@code NHttpRequestHandler}.
	 * @param serviceUrl
	 * @param handlerName
	 * @return NHttpRequestHandler
	 */
	NHttpRequestHandler getNHttpRequestHandler(ServiceUrl serviceUrl, String handlerName);
}
