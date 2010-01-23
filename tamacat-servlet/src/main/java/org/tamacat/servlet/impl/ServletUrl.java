package org.tamacat.servlet.impl;

import javax.servlet.http.HttpServlet;

public class ServletUrl {

	private String servletName;
	private String urlPattern;
	private HttpServlet servlet;
	
	public String getUrlPattern() {
		return urlPattern;
	}
	
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	public String getServletName() {
		return servletName;
	}
	
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public HttpServlet getServlet() {
		return servlet;
	}
	
	public void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}
}
