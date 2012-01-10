/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.page;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>It is the HTTP error page that used Velocity template.
 */
public class VelocityErrorPage {

	static final Log LOG = LogFactory.getLog(VelocityErrorPage.class);
	static final String LOGGER_NAME = "Velocity"; //VelocityErrorPage.class.getName();

    static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
    
    static final String DEFAULT_ERROR_500_HTML
		= "<html><body><p>500 Internal Server Error.</p></body></html>";
    
    private VelocityEngine velocityEngine;
    private Properties props;
	private String templatesPath = "templates";
	
	public VelocityErrorPage(Properties props) {
		this.props = props;
		init();
	}
	
	void init() {
		try {
			velocityEngine = new VelocityEngine();
			//velocityEngine.setApplicationAttribute(VelocityEngine.RESOURCE_LOADER,
	        //        new ClasspathResourceLoader());
			velocityEngine.setProperty("resource.loader", "error");
			velocityEngine.setProperty("error.resource.loader.instance",
					new ClasspathResourceLoader());
			velocityEngine.init(props);
			String path = props.getProperty("templates.path");
			if (path != null) {
				templatesPath = path;
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	public String getErrorPage(
			HttpRequest request, HttpResponse response,
			HttpException exception) {
		VelocityContext context = new VelocityContext();
		return getErrorPage(request, response, context, exception);
	}
	
	public String getErrorPage(
			HttpRequest request, HttpResponse response, 
			VelocityContext context, HttpException exception) {
    	response.setStatusCode(exception.getHttpStatus().getStatusCode());
		context.put("url", request.getRequestLine().getUri());
		context.put("method", request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH));
		context.put("exception", exception);
		if (LOG.isTraceEnabled() && exception.getHttpStatus().isServerError()) {
			exception.printStackTrace();
		}
    	try {
   			Template template = getTemplate(
   				"error" + exception.getHttpStatus().getStatusCode() + ".vm");
   			StringWriter writer = new StringWriter();
   			template.merge(context, writer);
   			return writer.toString();
    	} catch (Exception e) {
    		LOG.trace(e.getMessage());
    		return DEFAULT_ERROR_500_HTML;
    	}
    }
    
    protected Template getTemplate(String page) throws Exception {
    	return velocityEngine.getTemplate(templatesPath + "/" + page, "UTF-8");
    }
}
