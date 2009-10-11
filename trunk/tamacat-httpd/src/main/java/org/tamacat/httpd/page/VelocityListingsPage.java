/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.page;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.DateUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is the directory listings page that used Velocity template.
 */
public class VelocityListingsPage {

	static final Log LOG = LogFactory.getLog(VelocityListingsPage.class);
    static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
    
    static final String DEFAULT_ERROR_500_HTML
		= "<html><body><p>500 Internal Server Error.<br /></p></body></html>";
    private String listingsPage = "listings";
    
	public VelocityListingsPage() {}
	
	public VelocityListingsPage(Properties props) {
		try {
			Velocity.init(props);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	public String getListingsPage(
			HttpRequest request, HttpResponse response,
			File file) {
		VelocityContext context = new VelocityContext();
		return getListingsPage(request, response, context, file);
	}
	
	public String getListingsPage(
			HttpRequest request, HttpResponse response, 
			VelocityContext context, File file) {
		context.put("url", request.getRequestLine().getUri());
		if (request.getRequestLine().getUri().lastIndexOf('/') >= 0) {
			context.put("parent", "../");
		}
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return ! pathname.isHidden()
				&& ! pathname.getName().startsWith(".");
			}
		});
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		for (File f : files) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (f.isDirectory()) {
				map.put("getName", StringUtils.encode(f.getName(),"UTF-8") + "/");
				map.put("length", "-");
			} else {
				map.put("getName", StringUtils.encode(f.getName(), "UTF-8"));
				map.put("length", String.valueOf(f.length()));
			}
			map.put("isDirectory", String.valueOf(f.isDirectory()));
			map.put("lastModified", DateUtils.getTime(new Date(f.lastModified()), "yyyy-MM-dd HH:mm"));
			list.add(map);
		}
		context.put("list", list);
    	try {
   			Template template = getTemplate(listingsPage + ".vm");
   			StringWriter writer = new StringWriter();
   			template.merge(context, writer);
   			return writer.toString();
    	} catch (Exception e) {
    		LOG.debug(e.getMessage());
    		return DEFAULT_ERROR_500_HTML;
    	}
    }
    
    protected Template getTemplate(String page) throws Exception {
    	return Velocity.getTemplate("templates/" + page, "UTF-8");
    }
}
