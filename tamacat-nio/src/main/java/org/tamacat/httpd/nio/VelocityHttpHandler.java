/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityListingsPage;
import org.tamacat.httpd.page.VelocityPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;

/**
 * <p>It is implements of {@link HttpHandler} that uses {@code Apache Velocity}. 
 */
public class VelocityHttpHandler extends AbstractNHttpHandler {
    
	
	protected String welcomeFile = "index";
	private VelocityListingsPage listingPage = new VelocityListingsPage();
	protected boolean listings;
	
	private VelocityPage page;
	
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
	
	protected boolean useDirectoryListings() {
		if (listings) {
			return true;
		} else {
			return false;
		}
	}
	
	private VelocityPage getVelocityPage() {
		if (page == null) {
			page = new VelocityPage(this.docsRoot);
		}
		return page;
	}
	
	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		VelocityContext ctx = (VelocityContext) context.getAttribute(VelocityContext.class.getName());
		if (ctx == null) ctx = new VelocityContext();
		String path = RequestUtils.getRequestPath(request);
		ctx.put("param", RequestUtils.getParameters(context).getParameterMap());
		int idx = path.lastIndexOf(".html");
		if (idx >= 0) {
			//delete the extention of file name. (index.html -> index)
			setEntity(request, response, ctx, path.replace(".html", ""));
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
		String html = getVelocityPage().getPage(request, response, ctx, path);
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
			NStringEntity entity = new NStringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		NFileEntity body = new NFileEntity(file, getContentType(file));
        return body;
	}
}
