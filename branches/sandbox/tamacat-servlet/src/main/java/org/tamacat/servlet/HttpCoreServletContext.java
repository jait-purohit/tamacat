package org.tamacat.servlet;

import javax.servlet.ServletContext;

import org.tamacat.httpd.config.ServiceUrl;

public interface HttpCoreServletContext extends ServletContext {

	ServiceUrl getServiceUrl();
	
}
