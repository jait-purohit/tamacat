/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.URL;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.tamacat.httpd.core.HttpHandler;

public class ServiceUrl {
	public enum Type {
		NORMAL("normal"),
		REVERSE("reverse"),
		ERROR("error");
		
		private final String name;
		Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static Type find(String name) {
			return valueOf(name.toUpperCase());
		}
	}
	
	private URL host;
	private String handlerName;
	private String path;
	private ReverseUrl reverseUrl;
	private Type type;
	private final ServerConfig serverConfig;
	static XmlBeanFactory di
		= new XmlBeanFactory(new ClassPathResource("components.xml"));
	
	public ServiceUrl(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	public URL getHost() {
		return host;
	}

	public HttpHandler getHttpHandler() {
		HttpHandler httpHandler = (HttpHandler)di.getBean(getHandlerName());
		httpHandler.setServiceUrl(this);
		return httpHandler;
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
