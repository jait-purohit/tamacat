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

	ServiceUrl serviceUrl;
	String base;
	String a = "a";
	String p = "p";
	String prefix;
	String suffix;
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		VelocityContext ctx = new VelocityContext();
		String action = RequestUtils.getParameter(context, a);
		String process = RequestUtils.getParameter(context, p);
		String className = base + "." + prefix + action + suffix;
		Class<?> type = ClassUtils.forName(className);
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
