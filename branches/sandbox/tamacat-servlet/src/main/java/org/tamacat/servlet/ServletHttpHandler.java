package org.tamacat.servlet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.UriPatternMatcher;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.AbstractHttpHandler;
import org.tamacat.httpd.core.LocalFileHttpHandler;
import org.tamacat.servlet.impl.HttpServletObjectFactory;
import org.tamacat.servlet.impl.ServletConfigImpl;
import org.tamacat.servlet.impl.ServletContextImpl;
import org.tamacat.servlet.impl.ServletUrl;
import org.tamacat.servlet.xml.ServletDefine;
import org.tamacat.servlet.xml.ServletMapping;
import org.tamacat.servlet.xml.WebApp;
import org.tamacat.servlet.xml.WebXmlParser;
import org.tamacat.util.ClassUtils;

public class ServletHttpHandler extends AbstractHttpHandler {

	static final String WEB_XML_PATH = "WEB-INF/web.xml";
	
	private WebAppClassLoader loader;
	private HttpCoreServletContext servletContext;
	private HttpServletObjectFactory factory;
	private LocalFileHttpHandler localHandler;
	
	private Map<String, HttpServlet> servlets = new LinkedHashMap<String, HttpServlet>();
	private UriPatternMatcher matcher = new UriPatternMatcher();
	
	public ServletHttpHandler() {
		localHandler = new LocalFileHttpHandler();
	}

	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		super.setServiceUrl(serviceUrl);
		localHandler.setServiceUrl(serviceUrl);
		init();
	}
	
	@Override
	public void doRequest(HttpRequest request, HttpResponse response, HttpContext context) {
		ServletUrl servletUrl = getServletUrl(request.getRequestLine().getUri());
		if (servletUrl != null) {
			HttpServlet servlet = servletUrl.getServlet();
			HttpCoreServletRequest req
				= factory.createRequest(servletUrl, request, context);
			HttpCoreServletResponse res
				= factory.createResponse(response, context);
			try {
				servlet.service(req, res);
			} catch (Exception e) {
				throw new HttpCoreServletException(e);
			}
		} else {
			localHandler.doRequest(request, response, context);
		}
	}

	void init() {
		if (docsRoot == null) {
			setDocsRoot(serviceUrl.getPath()
				.replaceFirst("^/","").replaceFirst("/$", ""));
		}
		//TODO
		localHandler.setDocsRoot(docsRoot + "/..");
		String xml = docsRoot + "/" + WEB_XML_PATH;
		loader = new WebAppClassLoader(docsRoot, getClassLoader());
		WebApp webapp = new WebXmlParser(loader).parse(xml);
		createServletInstances(webapp);
	}
	
	protected ServletUrl getServletUrl(String path) {
		if (path.startsWith("/")) {
			return (ServletUrl) matcher.lookup(
				path.replace(serviceUrl.getPath(), "/")
			);
		} else {
			return (ServletUrl) matcher.lookup(path);
		}
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
				throw new HttpCoreServletException(e);
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
		HttpCoreServletContext servletContext = new ServletContextImpl(docsRoot, serviceUrl);
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
		Class<?> servletClass = ClassUtils.forName(define.getServletClass(), loader);
		HttpServlet servlet = (HttpServlet)	ClassUtils.newInstance(servletClass);
		return servlet;
	}
	
	ServletUrl getServletUrl(ServletMapping mapping) {
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setUrlPattern(mapping.getUrlPattern());
		servletUrl.setServletName(mapping.getServletName());
		return servletUrl;
	}

	@Override
	protected HttpEntity getEntity(String html) {
		StringEntity body = null;
		try {
			body = new StringEntity(html);
			body.setContentType(DEFAULT_CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			throw new HttpCoreServletException(e);
		}
        return body;
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, getContentType(file));
        return body;
	}
}
