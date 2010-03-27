/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityListingsPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpHandler} for local file access.
 */
public class LocalFileHttpHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(LocalFileHttpHandler.class);
	
	protected String welcomeFile = "index.html";
	private VelocityListingsPage listingPage = new VelocityListingsPage();
	protected boolean listings;
	
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
		if (listings && welcomeFile == null) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void doRequest(HttpRequest request, HttpResponse response, HttpContext context) {
		String uri = RequestUtils.getRequestPath(request);
		if (uri.endsWith("/") && useDirectoryListings() == false) {
			uri = uri + welcomeFile;
		}
		File file = new File(docsRoot, getDecodeUri(uri));
		// /// 404 NOT FOUND /////
		if (!file.exists()) {
			LOG.trace("File " + file.getPath() + " not found");
			throw new NotFoundException();
		}
		// /// 403 FORBIDDEN /////
		else if (!file.canRead() || file.isDirectory()) {
			if (file.isDirectory() && useDirectoryListings()) {
				String html = listingPage.getListingsPage(request, response,
						file);
				response.setStatusCode(HttpStatus.SC_OK);
				ResponseUtils.setEntity(response, getEntity(html));
			} else {
				LOG.trace("Cannot read file " + file.getPath());
				throw new ForbiddenException();
			}
		}
		// /// 200 OK /////
		else {
			LOG.trace("File " + file.getPath() + " found");
			response.setStatusCode(HttpStatus.SC_OK);
			ResponseUtils.setEntity(response, getFileEntity(file));
			LOG.trace("Serving file " + file.getPath());
		}
	}

	@Override
	protected HttpEntity getEntity(String html) {
		StringEntity body = null;
		try {
			body = new StringEntity(html);
			body.setContentType(DEFAULT_CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
		}
        return body;
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, getContentType(file));
        return body;
	}
}
