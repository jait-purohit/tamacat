package org.tamacat.servlet.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.servlet.HttpCoreServletContext;

public class ServletContextImpl implements HttpCoreServletContext {

	private static final Log LOG = LogFactory.getLog(ServletContextImpl.class);
	
	protected ServiceUrl serviceUrl;
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected Map<String, Servlet> servlets = new LinkedHashMap<String, Servlet>();
	protected Map<String, String> servletInfo = new LinkedHashMap<String, String>();

	protected Map<String, String> initParams = new LinkedHashMap<String, String>();
	
	ServletContextImpl(ServletEngine caller) {
		this.servlets = caller.servlets;
		this.serviceUrl = caller.serviceUrl;
	}
	
	ServletContextImpl(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public void addServlet(String servletName, Servlet servlet) {
		servlets.put(servletName, servlet);
	}
	
	public void removeServlet(String servletName) {
		servlets.remove(servletName);
	}
	
	@Override
	public ServiceUrl getServiceUrl() {
		return serviceUrl;
	}
	
	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	@Override
	public Enumeration<?> getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	@Override
	public ServletContext getContext(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		return serviceUrl.getPath().replaceFirst("/$", "");
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
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<?> getResourcePaths(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
		return servlets.get(name);
	}

	@Override
	public String getServletContextName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<?> getServletNames() {
		return Collections.enumeration(servletInfo.keySet());
	}

	@Override
	public Enumeration<?> getServlets() {
		return Collections.enumeration(servlets.values());
	}

	@Override
	public void log(String message) {
		LOG.info(message);
	}

	@Override
	public void log(Exception cause, String message) {
		LOG.error(message, cause);
	}

	@Override
	public void log(String message, Throwable cause) {
		LOG.error(message, cause);
	}
}
