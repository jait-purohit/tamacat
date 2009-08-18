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
	private ServiceType type;
	private final ServerConfig serverConfig;
	private HttpHandlerFactory factory;
	
	/**
	 * Constructor for ServiceConfig.
	 * @param serverConfig
	 */
	public ServiceUrl(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	/**
	 * <p>Set the {@link HttpHandlerFactory}.
	 * @param factory
	 */
	public void setHttpHandlerFactory(HttpHandlerFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * <p>Get the {@link HttpHandlerFactory}.
	 * If unset, use the {@link DefaultHttpHandlerFactory}. 
	 * @return
	 */
	protected HttpHandlerFactory getHttpHandlerFactory() {
		if (factory == null) {
			factory = new DefaultHttpHandlerFactory();
		}
		return factory;
	}
	/**
	 * <p>Get the URL of host.
	 * @return URL of host.
	 */
	public URL getHost() {
		return host;
	}

	/**
	 * <p>Get the HttpHandler from {@link HttpHandlerFactory#getHttpHandler}.
	 * @return HttpHandler
	 */
	public HttpHandler getHttpHandler() {
		return getHttpHandlerFactory().getHttpHandler(this, getHandlerName());
	}

	/**
	 * <p>Get the URL path.
	 * @return URL path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get the {@link ReverseUrl}. 
	 * @return ReverseUrl
	 */
	public ReverseUrl getReverseUrl() {
		return reverseUrl;
	}

	/**
	 * <p>Get the {@link ServiceConfig}.
	 * @return ServerConfig
	 */
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	/**
	 * <p>Get the type of Service URL.
	 * @return ServiceType
	 */
	public ServiceType getType() {
		return type;
	}
	
	/**
	 * <p>Check the ServiceType.
	 * @param type
	 * @return if ServiceType is equals, returns true
	 */
	public boolean isType(ServiceType type) {
		if (this.type == null) return false;
		return this.type.equals(type);
	}

	/**
	 * <p>Set the URL of host.
	 * @param host
	 */
	public void setHost(URL host) {
		this.host = host;
	}

	/**
	 * <p>Set the URL path.
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * <p>Set the {@link ReverseURL}.
	 * @param reverseUrl
	 */
	public void setReverseUrl(ReverseUrl reverseUrl) {
		this.reverseUrl = reverseUrl;
	}

	/**
	 * Set the {@link ServiceType}.
	 * @param type
	 */
	public void setType(ServiceType type) {
		this.type = type;
	}

	public String getHandlerName() {
		return handlerName;
	}

	/**
	 * <p>Get the name of the {@link HttpHandler}.
	 * @param handlerName
	 */
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
