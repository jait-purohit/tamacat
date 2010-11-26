/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import org.apache.http.nio.protocol.NHttpRequestHandler;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.config.ServiceUrl;

public class DefaultNHttpHandlerFactory implements NHttpHandlerFactory {
	private DIContainer di;
	
	public DefaultNHttpHandlerFactory(String xml) {
		di = DI.configure(xml, getClass().getClassLoader());
	}
	
	public DefaultNHttpHandlerFactory(String xml, ClassLoader loader) {
		di = DI.configure(xml, loader);
	}
	
	@Override
	public NHttpRequestHandler getNHttpRequestHandler(ServiceUrl serviceUrl,
			String handlerName) {
		NHttpHandler httpHandler = di.getBean(handlerName, NHttpHandler.class);
		httpHandler.setServiceUrl(serviceUrl);
		return httpHandler;
	}
}
