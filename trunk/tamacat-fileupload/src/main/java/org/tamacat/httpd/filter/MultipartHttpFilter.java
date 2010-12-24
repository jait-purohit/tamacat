/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.tamacat.util.FileUtils;

public class MultipartHttpFilter implements RequestFilter, ResponseFilter {

	static final Log LOG = LogFactory.getLog(MultipartHttpFilter.class);
	
	protected ServiceUrl serviceUrl;
	protected String baseDirectory;
	protected String encoding = "UTF-8";
	protected boolean writeFile;

	public void setWriteFile(boolean writeFile) {
		this.writeFile = writeFile;
	}

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
				throw new ServiceUnavailableException(e);
			} catch (Exception e) {
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
	
	@SuppressWarnings("unchecked")
	protected void handleFileItem(HttpContext context, FileItem item) {
		List<FileItem> list = (List<FileItem>)context.getAttribute(FileItem.class.getName());
		if (list == null) {
			list = new ArrayList<FileItem>();
		}
		list.add(item);
		context.setAttribute(FileItem.class.getName(), list);
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
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

	@SuppressWarnings("unchecked")
	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if (writeFile) {
			List<FileItem> list = (List<FileItem>) context.getAttribute(FileItem.class.getName());
			if (list != null) {
				try {
					for (FileItem item : list) {
						if (item.isFormField() == false) {
							writeFile(item, item.getName());
						}
					}
				} catch (IOException e) {
					throw new ServiceUnavailableException(e);
				}
			}
		}
	}
		
	protected void writeFile(FileItem item, String name) throws IOException {
		String fileName = getBaseDirectory() + "/"
							+ FileUtils.normalizeFileName(name);
		FileUtils.write(item.getInputStream(), new File(fileName));
		LOG.debug("save file: " + fileName);
	}
}
