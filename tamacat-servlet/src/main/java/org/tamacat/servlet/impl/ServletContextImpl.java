package org.tamacat.servlet.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
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
	
	private String contextRoot;
	protected ServiceUrl serviceUrl;
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected Map<String, Servlet> servlets = new LinkedHashMap<String, Servlet>();
	protected Map<String, String> servletInfo = new LinkedHashMap<String, String>();
	private Map<String, String> mimeTypes = new HashMap<String,String>();
	private Map<String, String> initParams = new LinkedHashMap<String, String>();
	
	private String servletContextName;
	private String serverInfo;
	
	ServletContextImpl(String contextRoot, ServiceUrl serviceUrl) {
		this.contextRoot = contextRoot;
		this.serviceUrl = serviceUrl;
	}
	
	public String getContextRoot() {
		if (contextRoot == null) {
			contextRoot = System.getProperty("user.dir") + "/webapps/"
			  + serviceUrl.getPath().replaceFirst("^/", "").replaceFirst("/$", "");
		}
		return contextRoot;
	}
	
	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}
	
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	public void addInitParam(String name, String value) {
		initParams.put(name, value);
	}
	
	public void addServlet(String servletName, Servlet servlet) {
		if (servlet != null) {
			servlets.put(servletName, servlet);
			servletInfo.put(servletName, servlet.getServletInfo());
		}
	}
	
	public void addMimeType(String file, String mimeType) {
		mimeTypes.put(file, mimeType);
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
		//TODO support cross context
		return this;
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
		return 2;
	}
	
	@Override
	public String getMimeType(String fileName) {
		if (fileName == null) return null;
		String mimeType = mimeTypes.get(fileName);
		if (mimeType == null && fileName.indexOf('.') >= 0) {
			String[] f = fileName.split("\\.");
			if (f.length >= 1) {
				String ext = f[f.length-1];
				mimeType = mimeTypes.get(ext);
			}
		}
		return mimeType;
	}

	@Override
	public int getMinorVersion() {
		return 5;
	}
	
	@Override
	public RequestDispatcher getNamedDispatcher(String servletName) {
		RequestDispatcher rd = new NamedRequestDispatcherImpl(servletName);
		return rd;
	}

	@Override
	public String getRealPath(String path) {
		if (path.indexOf("../") >= 0) {
			return null;
			//throw new IllegalArgumentException("The path format is invalid. [../]");
		}
		String p = getContextRoot() + "/" + path.replaceFirst("^/", "");
		return normalizePath(p);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher rd = new RequestDispatcherImpl(path);
		return rd;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		if (!(path.startsWith("/")) || path.indexOf("../") >= 0) {
			throw new IllegalArgumentException("The path format is invalid. [../]");
		}
		return getClass().getResource(
				normalizePath(getContextRoot() + path));
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		if (!(path.startsWith("/")) || path.indexOf("../") >= 0) {
			throw new IllegalArgumentException("The path format is invalid. [../]");
		}
		return getClass().getResourceAsStream(
				normalizePath(getContextRoot() + path));
	}

	@Override
	public Set<?> getResourcePaths(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerInfo() {
		return serverInfo;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
		return servlets.get(name);
	}

	@Override
	public String getServletContextName() {
		return servletContextName;
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
	
	static final char SEPARATOR = File.separatorChar;
	static final char TARGET_SEPARATOR = SEPARATOR == '\\' ? '/' :'\\'; 
	
	static String normalizePath(String path) {
		try {
			return new File(path).getCanonicalPath();
		} catch (IOException e) {
			return path;
		}
		//path.replace(TARGET_SEPARATOR, SEPARATOR);
	}
}
