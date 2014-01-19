/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import io.milton.config.HttpManagerBuilder;
import io.milton.http.HttpManager;
import io.milton.http.ResourceFactory;

public class WebDavHttpManager {

	HttpManager manager;
	public WebDavHttpManager(ResourceFactory resourceFactory) {
		HttpManagerBuilder builder = new HttpManagerBuilder();
		builder.setResourceFactory(resourceFactory);
		manager = builder.buildHttpManager();
	}

	public void process(WebDavHttpRequest request, WebDavHttpResponse response) {
		manager.process(request, response);
	}
}
