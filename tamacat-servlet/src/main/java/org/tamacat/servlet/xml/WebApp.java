package org.tamacat.servlet.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebApp {

	private String displayName;
	private String description;
	
	private Map<String,String> contextParams = new LinkedHashMap<String,String>();

	private List<ServletDefine> servlets = new ArrayList<ServletDefine>();
	private List<ServletMapping> servletMappings = new ArrayList<ServletMapping>();
	
	public Map<String, String> getContextParams() {
		return contextParams;
	}

	public void addContextParams(String name, String value) {
		this.contextParams.put(name, value);
	}

	public void setContextParams(Map<String, String> params) {
		this.contextParams = params;
	}

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
	
	public List<ServletMapping> getServletMapping() {
		return servletMappings;
	}
	
	public void addServletMapping(ServletMapping servletMapping) {
		this.servletMappings.add(servletMapping);
	}
}
