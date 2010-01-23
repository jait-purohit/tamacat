package org.tamacat.servlet.xml;

public class ServletMapping {

	//servlet-name
	private String servletName;
	
	//url-pattern
	private String urlPattern;
	
	public String getServletName() {
		return servletName;
	}
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	public String getUrlPattern() {
		return urlPattern;
	}
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
}
