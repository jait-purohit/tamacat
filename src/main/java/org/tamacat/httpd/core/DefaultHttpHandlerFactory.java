/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.config.ServiceUrl;

/**
 * <p>The default implements of {@link HttpHandlerFactory}.
 * Using the {@link DIContainer}, configuration file is {@code components.xml}.
 */
public class DefaultHttpHandlerFactory implements HttpHandlerFactory {

	static DIContainer di = DI.configure("components.xml");

	@Override
	public HttpHandler getHttpHandler(ServiceUrl serviceUrl, String handlerName) {
		HttpHandler httpHandler = di.getBean(handlerName, HttpHandler.class);
		httpHandler.setServiceUrl(serviceUrl);
		return httpHandler;
	}

}
