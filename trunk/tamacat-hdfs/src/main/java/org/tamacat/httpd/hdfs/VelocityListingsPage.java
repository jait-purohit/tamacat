/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.hdfs;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.DateUtils;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is the Hadoop HDFS directory listings page that used Velocity template.
 */
public class VelocityListingsPage {

	static final Log LOG = LogFactory.getLog(VelocityListingsPage.class);

    static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
    
    static final String DEFAULT_ERROR_500_HTML
		= "<html><body><p>500 Internal Server Error.<br /></p></body></html>";
    private String listingsPage = "listings";
    private VelocityEngine velocityEngine;

	public VelocityListingsPage() {
		try {
			Properties props = PropertyUtils.getProperties("velocity.properties");
			velocityEngine = new VelocityEngine();
			velocityEngine.setProperty("resource.loader", "list");
			velocityEngine.init(props);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	public String getListingsPage(
			HttpRequest request, HttpResponse response,
			FileSystem file, Path path) {
		VelocityContext context = new VelocityContext();
		return getListingsPage(request, response, context, file, path);
	}
	
	public String getListingsPage(
			HttpRequest request, HttpResponse response, 
			VelocityContext context, FileSystem file, Path path) {
		context.put("url", request.getRequestLine().getUri());
		if (request.getRequestLine().getUri().lastIndexOf('/') >= 0) {
			context.put("parent", "../");
		}
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		try {
			FileStatus[] files = file.listStatus(new Path(path.toUri().getPath()));
			if (files != null) {
				for (FileStatus f : files) {
					HashMap<String, String> map = new HashMap<String, String>();
					if (f.isDir()) {
						map.put("getName", StringUtils.encode(f.getPath().getName(),"UTF-8") + "/");
						map.put("length", "-");
					} else {
						map.put("getName", StringUtils.encode(f.getPath().getName(), "UTF-8"));
						map.put("length", String.valueOf(f.getLen()));
					}
					map.put("isDirectory", String.valueOf(f.isDir()));
					map.put("lastModified", DateUtils.getTime(new Date(f.getModificationTime()), "yyyy-MM-dd HH:mm"));
					list.add(map);
				}
			}
		} catch (IOException e) {
			throw new ServiceUnavailableException(e);
		}
		context.put("list", list);
    	try {
   			Template template = getTemplate(listingsPage + ".vm");
   			StringWriter writer = new StringWriter();
   			template.merge(context, writer);
   			return writer.toString();
    	} catch (Exception e) {
    		LOG.trace(e.getMessage());
    		return DEFAULT_ERROR_500_HTML;
    	}
    }
    
    protected Template getTemplate(String page) throws Exception {
    	return velocityEngine.getTemplate("templates/" + page, "UTF-8");
    }
}
