/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.hdfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UnixUserGroupInformation;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.core.AbstractHttpHandler;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.hdfs.VelocityListingsPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpHandler} for local file access.
 */
public class HdfsFileHttpHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(HdfsFileHttpHandler.class);
	
	protected String welcomeFile = "index.html";
	private VelocityListingsPage listingPage = new VelocityListingsPage();
	protected boolean listings;
	
	protected Configuration conf = new Configuration();
	
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
		String path = RequestUtils.getRequestPath(request);
		if (path.endsWith("/") && useDirectoryListings() == false) {
			path = path + welcomeFile;
		}
		conf.set(UnixUserGroupInformation.UGI_PROPERTY_NAME,"hadoop,supergroup");
		String uri = docsRoot + getDecodeUri(path);
		
		try {
			FileSystem file = FileSystem.get(URI.create(uri), conf);
			Path p = new Path(uri);
			///// 404 NOT FOUND /////
			if (file.exists(p) == false) {
				LOG.trace("File " + path + " not found");
				throw new NotFoundException();
			}
			///// FOR DIRECTORY /////
			else if (file.isFile(p) == false) {
				if (useDirectoryListings()) {
					String html = listingPage.getListingsPage(
							request, response, file, p);
					response.setStatusCode(HttpStatus.SC_OK);
					ResponseUtils.setEntity(response, getEntity(html));
				} else {
					///// 403 FORBIDDEN /////
					LOG.trace("Cannot read file " + file);
					throw new ForbiddenException();
				}
			}
			///// FOR FILE /////
			else {
				LOG.trace("File " + file + " found");
				response.setStatusCode(HttpStatus.SC_OK);
				ResponseUtils.setEntity(response, getFileEntity(file, p));
			}
		} catch (IOException e) {
			throw new ForbiddenException(e);
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
		return new FileEntity(file, getContentType(file));
	}
	
	protected HttpEntity getFileEntity(FileSystem fs, Path path) {
		InputStream in;
		try {
			in = fs.open(path);
		} catch (IOException e) {
			throw new ServiceUnavailableException(e);
		}
		return new InputStreamEntity(in, -1);
	}
}
