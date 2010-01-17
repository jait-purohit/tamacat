package org.tamacat.servlet.impl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;
import org.tamacat.servlet.xml.ServletDefine;
import org.tamacat.servlet.xml.WebApp;
import org.tamacat.servlet.xml.WebXmlParser;
import org.tamacat.util.ClassUtils;

public class ServletEngine {

	static final String WEB_XML_PATH = "WEB-INF/web.xml";
	
	private String path;
	private ServiceUrl serviceUrl;
	private HttpCoreServletContext servletContext;
	private HttpServletObjectFactory factory;

	private Map<String, Servlet> servlets = new LinkedHashMap<String, Servlet>();
	
	public ServletEngine(ServiceUrl serviceUrl) {
		this(null, serviceUrl);
	}
	
	public ServletEngine(String path, ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		if (path == null) {
			this.path = serviceUrl.getPath()
				.replaceFirst("^/","").replaceFirst("/$", "");
		} else {
			this.path = path;
		}
		String xml = this.path + "/" + WEB_XML_PATH;
		WebApp webapp = new WebXmlParser().parse(xml);
		createServletInstances(webapp);
	}

	public void processServlet(
				String servletName, HttpRequest req, HttpResponse res)
			throws IOException, ServletException {
		Servlet servlet = getServlet(servletName);
		if (servlet != null) {
			HttpCoreServletRequest request = factory.createRequest(req, new BasicHttpContext());
			HttpCoreServletResponse response = null;
			servlet.service(request, response);
		}
	}
	
	protected Servlet getServlet(String name) {
		return servlets.get(name);
	}

	protected void createServletInstances(WebApp webapp) {
		this.servletContext = new ServletContextImpl(path, serviceUrl);
		servletContext.setServletContextName(webapp.getDisplayName());
		String serverInfo = serviceUrl.getServerConfig().getParam("ServerName");
		servletContext.setServerInfo(serverInfo);
		
		List<ServletDefine> servletDefines = webapp.getServlets();
		for (ServletDefine define : servletDefines) {
			Class<?> servletClass = ClassUtils.forName(define.getServletClass());
			Servlet servlet = (Servlet)	ClassUtils.newInstance(servletClass);
			servlets.put(define.getServletName(), servlet);
			servletContext.addServlet(define.getServletName(), servlet);
		}
		factory = new HttpServletObjectFactory(servletContext);
	}
}
