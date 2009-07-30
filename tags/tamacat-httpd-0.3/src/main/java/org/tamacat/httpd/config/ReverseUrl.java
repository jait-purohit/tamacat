/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.InetSocketAddress;
import java.net.URL;


/**
 * The ReverseUrl interface is a setup of Reverse Proxy URL.
 */
public interface ReverseUrl {
	
	ServiceUrl getServiceUrl();

	URL getHost();

	String getPath();

	URL getReverse();
	
	void setReverse(URL url);
	
	/**
	 * Get the ReverseUrl with path.
	 * @param path
	 * @return URL
	 */
	URL getReverseUrl(String path);
	
	/**
	 * Get the convert requested URL with path.
	 * It uses at the time of Location header conversion.
	 * @param path
	 * @return
	 */
	String getConvertRequestedUrl(String path);
	
	/**
	 * Get the backend server's InetSocketAddress.
	 * @return InetSocketAddress
	 */
	InetSocketAddress getTargetAddress();
	
}
