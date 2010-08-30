package org.tamacat.servlet.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;
import org.tamacat.servlet.ServletHttpHandler;

public class RequestDispatcherImpl implements RequestDispatcher {

	protected String path;
	protected ServletHttpHandler handler;

	RequestDispatcherImpl(ServletHttpHandler handler) {
		this.handler = handler;
	}
	
	RequestDispatcherImpl(String path) {
		this.handler = new ServletHttpHandler();
		this.path = path;
	}
	
	public void setServiceUrl(ServiceUrl serviceUrl) {
		handler.setServiceUrl(serviceUrl);
	}
	
	@Override
	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		HttpRequest req = ((HttpCoreServletRequest)request).getHttpRequest();
		HttpResponse res = ((HttpCoreServletResponse)response).getHttpResponse();
		HttpContext context = ((HttpCoreServletRequest)request).getHttpContext();
		handler.handle(req, res, context);
	}

	@Override
	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		HttpRequest req = ((HttpCoreServletRequest)request).getHttpRequest();
		HttpResponse res = ((HttpCoreServletResponse)response).getHttpResponse();
		HttpContext context = ((HttpCoreServletRequest)request).getHttpContext();
		handler.handle(req, res, context);
	}

}
