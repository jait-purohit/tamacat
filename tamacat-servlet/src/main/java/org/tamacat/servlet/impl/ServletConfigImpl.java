package org.tamacat.servlet.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletConfigImpl implements ServletConfig {

	private ServletContext context;
	private String servletName;

	private Map<String,String> initParams = new LinkedHashMap<String,String>();

	public ServletConfigImpl(ServletContext context) {
		this.context = context;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public void setInitParams(Map<String,String> initParams) {
		this.initParams = initParams;
	}
	
	@Override
	public String getInitParameter(String name) {
		return initParams.get(name);
	}

	@Override
	public Enumeration<?> getInitParameterNames() {
		return Collections.enumeration(initParams.keySet());
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public String getServletName() {
		return servletName;
	}
}
