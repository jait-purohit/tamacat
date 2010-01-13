/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.URL;
import org.tamacat.httpd.core.HttpHandler;

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
	
	/**
	 * <p>Constructor for ServiceConfig.
	 * @param serverConfig
	 */
	public ServiceUrl(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	/**
	 * <p>Returns the URL of host.
	 * @return URL of host.
	 */
	public URL getHost() {
		return host;
	}

	/**
	 * <p>Returns the URL path.
	 * @return URL path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * <p>Returns the {@link ReverseUrl}. 
	 * @return ReverseUrl
	 */
	public ReverseUrl getReverseUrl() {
		return reverseUrl;
	}

	/**
	 * <p>Returns the {@link ServiceConfig}.
	 * @return ServerConfig
	 */
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	/**
	 * <p>Returns the type of Service URL.
	 * @return ServiceType
	 */
	public ServiceType getType() {
		return type;
	}
	
	/**
	 * <p>Check the {@code ServiceType}.
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
	 * <p>Set the {@link ServiceType}.
	 * @param type
	 */
	public void setType(ServiceType type) {
		this.type = type;
	}

	/**
	 * <p>Returns the handler name.
	 */
	public String getHandlerName() {
		return handlerName;
	}

	/**
	 * <p>Set the name of the {@link HttpHandler}.
	 * @param handlerName
	 */
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
