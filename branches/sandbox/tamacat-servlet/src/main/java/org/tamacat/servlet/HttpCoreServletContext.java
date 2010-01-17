package org.tamacat.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.tamacat.httpd.config.ServiceUrl;

public interface HttpCoreServletContext extends ServletContext {

	ServiceUrl getServiceUrl();
	
	void setServletContextName(String servletContextName);
	
	void setServerInfo(String serverInfo);
	
	void addServlet(String servletName, Servlet servlet);
	void removeServlet(String servletName);
	
	void addMimeType(String file, String mimeType);
}
