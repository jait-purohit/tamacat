package org.tamacat.servlet.xml;

import java.util.ArrayList;
import java.util.List;

public class WebApp {

	private String displayName;
	private String description;
	
	private List<ServletDefine> servlets = new ArrayList<ServletDefine>();
	private ServletMapping servletMapping = new ServletMapping();
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = (displayName != null ? displayName.trim() : null);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = (description != null ? description.trim() : null);
	}
	
	public List<ServletDefine> getServlets() {
		return servlets;
	}
	
	public void setServlets(List<ServletDefine> servlets) {
		this.servlets = servlets;
	}
	
	public ServletMapping getServletMapping() {
		return servletMapping;
	}
	
	public void setServletMapping(ServletMapping servletMapping) {
		this.servletMapping = servletMapping;
	}
}
