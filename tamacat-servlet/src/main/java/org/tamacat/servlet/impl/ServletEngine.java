package org.tamacat.servlet.impl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;
import org.tamacat.servlet.xml.ServletDefine;
import org.tamacat.servlet.xml.ServletMapping;
import org.tamacat.servlet.xml.WebApp;
import org.tamacat.servlet.xml.WebXmlParser;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.StringUtils;

public class ServletEngine {

	static final String WEB_XML_PATH = "WEB-INF/web.xml";
	
	private String path;
	private ServiceUrl serviceUrl;
	private HttpCoreServletContext servletContext;
	private HttpServletObjectFactory factory;

	private Map<String, Servlet> servlets = new LinkedHashMap<String, Servlet>();
	private Map<String, ServletUrl> servletMappings = new LinkedHashMap<String, ServletUrl>();
	
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
			HttpCoreServletRequest request = factory.createRequest(
					servlet, req, new BasicHttpContext());
			HttpCoreServletResponse response = null;
			servlet.service(request, response);
		}
	}
	
	protected Servlet getServlet(String name) {
		ServletUrl url = servletMappings.get(name);
		return url != null ? url.getServlet() : null;
	}

	protected void createServletInstances(WebApp webapp) {
		this.servletContext = createServletContext(webapp);
		List<ServletDefine> servletDefines = webapp.getServlets();
		for (ServletDefine define : servletDefines) {
			ServletConfig config = createServletConfig(define, servletContext);
			try {
				Servlet servlet = createServlet(define);
				servlet.init(config);
				servlets.put(define.getServletName(), servlet);
				servletContext.addServlet(define.getServletName(), servlet);
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
		
		List<ServletMapping> mappings = webapp.getServletMapping();
		for (ServletMapping mapping : mappings) {
			ServletUrl servletUrl = getServletUrl(mapping);
			//@SuppressWarnings("unused")
			//Servlet servlet = servlets.get(mapping.getServletName());
			//servlet.setServletMapping(mapping);
			servletMappings.put(mapping.getServletName(), servletUrl);
		}
		factory = new HttpServletObjectFactory(servletContext);
	}
	
	protected HttpCoreServletContext createServletContext(WebApp webapp) {
		HttpCoreServletContext servletContext = new ServletContextImpl(path, serviceUrl);
		servletContext.setServletContextName(webapp.getDisplayName());
		String serverInfo = serviceUrl.getServerConfig().getParam("ServerName");
		servletContext.setServerInfo(serverInfo);
		return servletContext;
	}

	protected ServletConfig createServletConfig(ServletDefine define, ServletContext context) {
		ServletConfigImpl config = new ServletConfigImpl(context);
		config.setServletName(define.getServletName());
		config.setInitParams(define.getInitParams());
		return config;
	}
	
	protected Servlet createServlet(ServletDefine define) throws ServletException {
		Class<?> servletClass = ClassUtils.forName(define.getServletClass());
		Servlet servlet = (Servlet)	ClassUtils.newInstance(servletClass);
		return servlet;
	}
	
	ServletUrl getServletUrl(ServletMapping mapping) {
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setUrlPattern(mapping.getUrlPattern());
		servletUrl.setServletName(mapping.getServletName());
		return servletUrl;
	}
	
    String normalizePattern(String p) {
        if (StringUtils.isNotEmpty(p) && !p.startsWith("/")
        		&& !p.startsWith("*"))
            return "/" + p;
        return p;
    }
}
