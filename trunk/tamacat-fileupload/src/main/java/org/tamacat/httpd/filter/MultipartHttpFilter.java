/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.mime.HttpFileUpload;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.IOUtils;

public class MultipartHttpFilter implements RequestFilter, ResponseFilter {

	static final Log LOG = LogFactory.getLog(MultipartHttpFilter.class);
	
	protected ServiceUrl serviceUrl;
	protected String baseDirectory;
	protected String encoding;
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if (RequestUtils.isMultipart(request)) {
			try {
				HttpFileUpload upload = new HttpFileUpload();
				if (encoding != null ) {
					upload.setHeaderEncoding(encoding);
				}
				List<FileItem> list = upload.parseRequest(request);
				for (FileItem item : list) {
					if (item.isFormField()) {
						handleFormField(context, item);
					} else {
						handleFileItem(context, item);
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				throw new ServiceUnavailableException(e);
			} catch (Exception e) {
				LOG.debug(e);
				throw new ServiceUnavailableException(e);
			}
		}
	}
	
	protected void handleFormField(HttpContext context, FileItem item) {
		String key = item.getFieldName();
		try {
			String value = null;
			if (encoding != null) {
			    value = item.getString(encoding);
			} else {
				value = item.getString();
			}
			RequestUtils.getParameters(context)
				.getParameterMap().put(key, Arrays.asList(value));
		} catch (UnsupportedEncodingException e) {
		}
	}
	
	protected void handleFileItem(HttpContext context, FileItem item) {
		context.setAttribute(FileItem.class.getName(), item);
	}
		
	protected void writeFile(FileItem item, String name) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(
				new File(getBaseDirectory() + "/" + normalizeFileName(name)));
			InputStream in = new BufferedInputStream(item.getInputStream());
			byte[] fbytes = new byte[1024];
			while ((in.read(fbytes)) >= 0) {
				out.write(fbytes);
			}
		} catch (IOException e) {
			throw new ServiceUnavailableException(e);
		} finally {
			IOUtils.close(out);
		}
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	protected String normalizeFileName(String fileName) {
		return fileName != null ? 
			fileName.replace("..", "").replace("//","/")
			.replace("\r","").replace("\n","") : null;
	}
	
	protected String getBaseDirectory() {
		return baseDirectory;
	}
	
	public void setBaseDirectory(String baseDirectory) {
		if (baseDirectory != null) {
			baseDirectory.replace("\\", "/");
			this.baseDirectory = baseDirectory.replaceFirst("/$", "");
		}
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		FileItem item = (FileItem) context.getAttribute(FileItem.class.getName());
		if (item != null) {
			writeFile(item, item.getName());
		}
	}
}
