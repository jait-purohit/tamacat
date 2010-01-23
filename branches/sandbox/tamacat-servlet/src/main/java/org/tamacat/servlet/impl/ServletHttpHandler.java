package org.tamacat.servlet.impl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.UriPatternMatcher;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.HttpHandler;
import org.tamacat.httpd.exception.HttpStatus;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.HttpCoreServletResponse;
import org.tamacat.servlet.xml.ServletDefine;
import org.tamacat.servlet.xml.ServletMapping;
import org.tamacat.servlet.xml.WebApp;
import org.tamacat.servlet.xml.WebXmlParser;
import org.tamacat.util.ClassUtils;

public class ServletHttpHandler implements HttpHandler {

	static final String WEB_XML_PATH = "WEB-INF/web.xml";
	
	private String path;
	private ServiceUrl serviceUrl;
	private HttpCoreServletContext servletContext;
	private HttpServletObjectFactory factory;

	private Map<String, HttpServlet> servlets = new LinkedHashMap<String, HttpServlet>();
	private UriPatternMatcher matcher = new UriPatternMatcher();
	
	public ServletHttpHandler() {}
	
	public ServletHttpHandler(String path) {
		this.path = path;
	}
	
	@Override
	public void setRequestFilter(RequestFilter filter) {
	}

	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		init();
	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		HttpServlet servlet = getServlet(request.getRequestLine().getUri());
		if (servlet != null) {
			HttpCoreServletRequest req
				= factory.createRequest(servlet, request, context);
			HttpCoreServletResponse res = null;
			try {
				servlet.service(req, res);
			} catch (ServletException e) {
				throw new org.tamacat.httpd.exception.HttpException(
						HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
			}
		} else {
			throw new NotFoundException();
		}
	}

	void init() {
		if (path == null) {
			this.path = serviceUrl.getPath()
				.replaceFirst("^/","").replaceFirst("/$", "");
		}
		String xml = this.path + "/" + WEB_XML_PATH;
		WebApp webapp = new WebXmlParser().parse(xml);
		createServletInstances(webapp);
	}
	
	protected HttpServlet getServlet(String path) {
		ServletUrl url = (ServletUrl) matcher.lookup(path);
		return url != null ? url.getServlet() : null;
	}
	
	protected HttpServlet getServletFromName(String name) {
		return servlets.get(name);
	}

	protected void createServletInstances(WebApp webapp) {
		this.servletContext = createServletContext(webapp);
		List<ServletDefine> servletDefines = webapp.getServlets();
		for (ServletDefine define : servletDefines) {
			ServletConfig config = createServletConfig(define, servletContext);
			try {
				HttpServlet servlet = createServlet(define);
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
			HttpServlet servlet = servlets.get(mapping.getServletName());
			servletUrl.setServlet(servlet);
			matcher.register(mapping.getUrlPattern(), servletUrl);
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
	
	protected HttpServlet createServlet(ServletDefine define) throws ServletException {
		Class<?> servletClass = ClassUtils.forName(define.getServletClass());
		HttpServlet servlet = (HttpServlet)	ClassUtils.newInstance(servletClass);
		return servlet;
	}
	
	ServletUrl getServletUrl(ServletMapping mapping) {
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setUrlPattern(mapping.getUrlPattern());
		servletUrl.setServletName(mapping.getServletName());
		return servletUrl;
	}
}
