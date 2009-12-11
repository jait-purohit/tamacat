package org.tamacat.servlet.xml;

public class ServletDefine {
	
	private String servletName;
	private String servletClass;
	
	public String getServletName() {
		return servletName;
	}
	
	public void setServletName(String servletName) {
		this.servletName = (servletName != null ? servletName.trim() : null);
	}
	
	public String getServletClass() {
		return servletClass;
	}
	
	public void setServletClass(String servletClass) {
		this.servletClass = (servletClass != null ? servletClass.trim() : null);
	}
}
