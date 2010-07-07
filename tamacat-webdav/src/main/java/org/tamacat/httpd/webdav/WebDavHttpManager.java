/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.ResourceFactory;

public class WebDavHttpManager extends HttpManager {

	public WebDavHttpManager(ResourceFactory resourceFactory) {
		super(resourceFactory);
	}

}
