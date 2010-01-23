package org.tamacat.servlet.xml;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServletDefine {
	
	private String servletName;
	private String servletClass;
	
	private Map<String,String> initParams = new LinkedHashMap<String,String>();

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
	
	public Map<String, String> getInitParams() {
		return initParams;
	}
	
	public void setInitParams(Map<String, String> params) {
		this.initParams = params;
	}
	
	public void addInitParams(String name, String value) {
		this.initParams.put(name, value);
	}
}
