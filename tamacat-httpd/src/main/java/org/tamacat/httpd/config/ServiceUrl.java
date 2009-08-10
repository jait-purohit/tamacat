/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.URL;

import org.tamacat.httpd.core.DefaultHttpHandlerFactory;
import org.tamacat.httpd.core.HttpHandler;
import org.tamacat.httpd.core.HttpHandlerFactory;

/**
 * <p>It is setting of the service URL.
 */
public class ServiceUrl {
	
	private URL host;
	private String handlerName;
	private String path;
	private ReverseUrl reverseUrl;
	private Type type;
	private final ServerConfig serverConfig;
	private HttpHandlerFactory factory;
	
	public ServiceUrl(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	public void setHttpHandlerFactory(HttpHandlerFactory factory) {
		this.factory = factory;
	}
	
	protected HttpHandlerFactory getHttpHandlerFactory() {
		if (factory == null) {
			factory = new DefaultHttpHandlerFactory();
		}
		return factory;
	}
	
	public URL getHost() {
		return host;
	}

	public HttpHandler getHttpHandler() {
		return getHttpHandlerFactory().getHttpHandler(this, getHandlerName());
	}

	public String getPath() {
		return path;
	}

	public ReverseUrl getReverseUrl() {
		return reverseUrl;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public Type getType() {
		return type;
	}

	public boolean isType(Type type) {
		if (this.type == null) return false;
		return this.type.equals(type);
	}

	public void setHost(URL host) {
		this.host = host;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setReverseUrl(ReverseUrl reverseUrl) {
		this.reverseUrl = reverseUrl;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
