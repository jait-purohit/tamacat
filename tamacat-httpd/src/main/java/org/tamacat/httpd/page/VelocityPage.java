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
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class VelocityPage {
	static final Log LOG = LogFactory.getLog(VelocityPage.class);
    
	public VelocityPage() {}
	
	public VelocityPage(Properties props) {
		try {
			Velocity.init(props);
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
    	return Velocity.getTemplate(page, "UTF-8");
    }
}
