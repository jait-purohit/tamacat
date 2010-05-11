/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.lang.reflect.Method;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.ClassUtils;

public class VelocityActionFilter implements RequestFilter {

	private ServiceUrl serviceUrl;
	private String base;
	private String actionKeyName = "a";
	
	public ServiceUrl getServiceUrl() {
		return serviceUrl;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getActionKeyName() {
		return actionKeyName;
	}

	public void setActionKeyName(String actionKeyName) {
		this.actionKeyName = actionKeyName;
	}

	public String getProcessKeyName() {
		return processKeyName;
	}

	public void setProcessKeyName(String processKeyName) {
		this.processKeyName = processKeyName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	private String processKeyName = "p";
	private String prefix;
	private String suffix;
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		VelocityContext ctx = new VelocityContext();
		String action = RequestUtils.getParameter(context, actionKeyName);
		String process = RequestUtils.getParameter(context, processKeyName);
		if (action != null) action = "";
		String className = base + "." + prefix + action + suffix;
		Class<?> type = ClassUtils.forName(className);
		System.out.println(type);

		Object instance = ClassUtils.newInstance(type);
		Method method = ClassUtils.getMethod(type, process);
		ClassUtils.invoke(method, instance);
		context.setAttribute(VelocityContext.class.getName(), ctx);
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}
