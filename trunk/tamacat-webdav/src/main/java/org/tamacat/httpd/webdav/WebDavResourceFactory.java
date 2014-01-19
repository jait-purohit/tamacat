/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.File;

import org.tamacat.httpd.config.ServiceUrl;

import io.milton.http.ResourceFactory;
import io.milton.http.fs.FileSystemResourceFactory;
import io.milton.http.fs.FsDirectoryResource;
import io.milton.http.fs.FsFileResource;
import io.milton.http.fs.FsResource;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.fs.SimpleFileContentService;

public class WebDavResourceFactory implements ResourceFactory {

	private String docsRoot;
	private FileSystemResourceFactory factory;
	private ServiceUrl serviceUrl;

	public void setDocsRoot(String docsRoot) {
		this.docsRoot = docsRoot;
	}

	public WebDavResourceFactory(ServiceUrl serviceUrl, String docsRoot) {
		this.serviceUrl = serviceUrl;
		this.docsRoot = docsRoot;

		io.milton.http.SecurityManager securityManager = new NullSecurityManager();//SimpleSecurityManager();
		File file = new File(docsRoot);
		factory = new FileSystemResourceFactory(file, securityManager, ".");
	}

	@Override
	public FsResource getResource(String host, String url) {
		url = stripContext(url);
		File file = new File(docsRoot + url);
//		if (file.canRead() == false) {
//			throw new NotFoundException(url);
//		}
		SimpleFileContentService fc = new SimpleFileContentService();
		if (file.isDirectory()) {
			return new FsDirectoryResource(host, factory, file, fc);
		} else {
			return new FsFileResource(host, factory, file, fc);
		}
	}

	private String stripContext(String url) {
		return url.replaceFirst( '/' + serviceUrl.getPath(), "");
	}
}
