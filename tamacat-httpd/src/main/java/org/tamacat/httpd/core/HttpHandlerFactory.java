/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.tamacat.httpd.config.ServiceUrl;

public interface HttpHandlerFactory {

	HttpHandler getHttpHandler(ServiceUrl serviceUrl, String handlerName);
}
