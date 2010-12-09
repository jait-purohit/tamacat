/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityListingsPage;
import org.tamacat.httpd.page.VelocityPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.util.PropertyUtils;

/**
 * <p>It is implements of {@link HttpHandler} that uses {@code Apache Velocity}. 
 */
public class VelocityHttpHandler extends AbstractHttpHandler {
	
	protected String welcomeFile = "index";
	private VelocityListingsPage listingPage;
	protected boolean listings;
	private VelocityPage page;
	private final Set<String> urlPatterns = new HashSet<String>();
	
	public void setUrlPatterns(String patterns) {
		for (String pattern : patterns.split(",")) {
			urlPatterns.add(pattern.trim());
		}
	}
	
	public boolean isMatchUrlPattern(String path) {
		if (urlPatterns.size() > 0) {
			for (String pattern : urlPatterns) {
				if (pattern.endsWith("/") && path.matches(pattern)) {
					return true;
				} else if (path.lastIndexOf(pattern) >= 0) {
					return true;
				}
			}
		} else if (path.lastIndexOf(".html") >= 0) {
			return true;
		}
		return false;
	}
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	super.setServiceUrl(serviceUrl);
    	Properties props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
		listingPage = new VelocityListingsPage(props);
		page = new VelocityPage(props);
		page.init(this.docsRoot);
	}
	
	/**
	 * <p>Set the welcome file.
	 * This method use after {@link #setListings}.
	 * @param welcomeFile
	 */
	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}
	
	/**
	 * <p>Should directory listings be produced
	 * if there is no welcome file in this directory.</p>
	 * 
	 * <p>The welcome file becomes unestablished when I set true.<br>
	 * When I set the welcome file, please set it after having
	 * carried out this method.</p>
	 * 
	 * @param listings true: directory listings be produced (if welcomeFile is null). 
	 */
	public void setListings(boolean listings) {
		this.listings = listings;
		if (listings) {
			this.welcomeFile = null;
		}
	}
	
	public void setListingsPage(String listingsPage) {
		listingPage.setListingsPage(listingsPage);
	}
	
	protected boolean useDirectoryListings() {
		if (listings) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		VelocityContext ctx = (VelocityContext) context.getAttribute(VelocityContext.class.getName());
		if (ctx == null) ctx = new VelocityContext();
		String path = RequestUtils.getRequestPath(request);
		ctx.put("param", RequestUtils.getParameters(context).getParameterMap());
		ctx.put("contextRoot", serviceUrl.getPath().replaceFirst("/$",""));
		if (isMatchUrlPattern(path)) {
			//delete the extention of file name. (index.html -> index)
			String file = path.indexOf(".")>=0 ? path.split("\\.")[0] : path;
			setEntity(request, response, ctx, file);
		} else if (path.endsWith("/")) {
			//directory -> index page.
			File file = null;
			if (path.endsWith("/")) {
				if (welcomeFile == null) {
					welcomeFile = "index.vm";
				}
				file = new File(docsRoot + getDecodeUri(path + welcomeFile));
			}
			if (useDirectoryListings() && file.canRead() == false) {
				file = new File(docsRoot + getDecodeUri(path));
				setListFileEntity(request, response, file);
			} else {
				setEntity(request, response, ctx, path + "index");
			}
		} else {
			//get the file in this server.
			setFileEntity(request, response, path);
		}
	}
	
	private void setListFileEntity(HttpRequest request, HttpResponse response, File file) {
		try {
			String html = listingPage.getListingsPage(
					request, response, file);
			ResponseUtils.setEntity(response, getEntity(html));
			response.setStatusCode(HttpStatus.SC_OK);
		} catch (Exception e) {
			throw new NotFoundException(e);
		}
	}
	
	private void setEntity(HttpRequest request, HttpResponse response, VelocityContext ctx, String path) {
		String html = page.getPage(request, response, ctx, path);
		ResponseUtils.setEntity(response, getEntity(html));
	}
	
	private void setFileEntity(HttpRequest request, HttpResponse response, String path) {
		try {
			File file = new File(docsRoot + getDecodeUri(path));//r.toURI());
			if (file.exists() == false) {
				throw new NotFoundException();
			}
			ResponseUtils.setEntity(response, getFileEntity(file));
		} catch (Exception e) {
			throw new NotFoundException(e);
		}
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, getContentType(file));
        return body;
	}
}
