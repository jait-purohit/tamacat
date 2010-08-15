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

	private DIContainer di;
	
	public DefaultHttpHandlerFactory() {
		di = DI.configure("components.xml");
	}
	
	public DefaultHttpHandlerFactory(ClassLoader loader) {
		di = DI.configure("components.xml", loader);
	}
	
	@Override
	public HttpHandler getHttpHandler(ServiceUrl serviceUrl) {
		HttpHandler httpHandler = di.getBean(serviceUrl.getHandlerName(), HttpHandler.class);
		httpHandler.setServiceUrl(serviceUrl);
		return httpHandler;
	}
}
