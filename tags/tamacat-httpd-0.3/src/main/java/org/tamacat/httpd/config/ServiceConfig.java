/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tamacat.httpd.config.ServiceUrl.Type;

public class ServiceConfig {

	private List<ServiceUrl> reverseUrls = new ArrayList<ServiceUrl>();
	private Map<String,ServiceUrl> regulars = new HashMap<String, ServiceUrl>();
	private Map<String,ServiceUrl> reverses = new HashMap<String, ServiceUrl>();
	
	/**
	 * <p>The method which acquires list of {@link ServiceUrl}.
	 * @return {@literal List<ServiceUrl>}
	 */
	public List<ServiceUrl> getServiceUrlList() {
		return reverseUrls;
	}
	
	/**
	 * <p>The method which acquires {@link ServiceUrl}.
	 * @param path
	 * @return {@literal ServiceUrl}
	 */
	public ServiceUrl getServiceUrl(String path) {
		return regulars.get(path);
	}

	/**
	 * <p>The method which registers ServiceUrl.
	 * @param serviceUrl {@link ServiceUrl}
	 */
	public void addServiceUrl(ServiceUrl serviceUrl) {
		regulars.put(serviceUrl.getPath(), serviceUrl);
		if (serviceUrl.isType(Type.REVERSE)) {
			reverses.put(serviceUrl.getReverseUrl().getReverse().getPath(), serviceUrl);
		}
		reverseUrls.add(serviceUrl);
	}
	
	/**
	 * <p>The method which unregisters ServiceUrl.
	 * @param serviceUrl
	 */
	public void removeServiceUrl(ServiceUrl serviceUrl) {
		regulars.remove(serviceUrl.getPath());
		if (serviceUrl.isType(Type.REVERSE)) {
			reverses.remove(serviceUrl.getReverseUrl().getReverse().getPath());
		}
		reverseUrls.remove(serviceUrl);
	}
}
