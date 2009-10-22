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
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;

/**
 * <p>It is the HTTP page that used Velocity template.
 */
public class VelocityPage {
	static final Log LOG = LogFactory.getLog(VelocityPage.class);
	static final String LOGGER_NAME = "Velocity"; //VelocityPage.class.getName();
	
    private VelocityEngine velocityEngine;
    
	public VelocityPage() {
		init(PropertyUtils.getProperties("server.properties"));
	}
	
	public VelocityPage(Properties props) {
		init(props);
	}
	
	void init(Properties props) {
		try {
			velocityEngine = new VelocityEngine();//props);
			velocityEngine.setProperty("velocimacro.library","");
//			velocityEngine.setProperty("resource.loader", "page");
			//velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "page");
//			velocityEngine.setProperty("page.resource.loader.class", 
//				"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
//			velocityEngine.setProperty("file.resource.loader.path", ".");
			velocityEngine.setProperty(
				RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
			velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", LOGGER_NAME);
			velocityEngine.init();
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}
	
	public String getPage(HttpRequest request, HttpResponse response, String page) {
		VelocityContext context = new VelocityContext();
		return getPage(request, response, context, page);
	}
	
	public String getPage(HttpRequest request, HttpResponse response, 
			VelocityContext	context, String page) {
		context.put("url", request.getRequestLine().getUri());
		context.put("method", request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH));
    	try {
   			Template template = getTemplate(page + ".vm");
   			StringWriter writer = new StringWriter();
   			template.merge(context, writer);
   			return writer.toString();
    	} catch (ResourceNotFoundException e) {
    		throw new NotFoundException(e);
    	} catch (Exception e) {
    		throw new ServiceUnavailableException(e);
    	}
    }
    
    protected Template getTemplate(String page) throws Exception {
    	return velocityEngine.getTemplate(page, "UTF-8");
    }
}
