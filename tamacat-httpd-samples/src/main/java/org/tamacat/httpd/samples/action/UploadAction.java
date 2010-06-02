/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.samples.action;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.fileupload.FileItem;
import org.apache.hadoop.fs.FileSystem;

import org.tamacat.httpd.core.RequestContext;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.hdfs.util.HdfsFileUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;

public class UploadAction {

	static final Log LOG = LogFactory.getLog(UploadAction.class);
	
	static String nameNodeUrl;
	static {
		Properties props = PropertyUtils.getProperties("hdfs.properties");
		nameNodeUrl = props.getProperty("hdfs.base.uri", "hdfs://localhost/user/hadoop");
	}
	
	@SuppressWarnings("unchecked")
	public void upload(RequestContext request) {
		List<FileItem> list = (List<FileItem>) request.getAttribute(FileItem.class.getName());
		if (list != null) {
			try {
				for (FileItem item : list) {
					if (item.isFormField() == false) {
						HdfsFileUtils.write(item.getInputStream(), 
						item.getName());
						LOG.info("UPLOAD: " + item.getName());
					}
				}
			} catch (IOException e) {
				throw new ServiceUnavailableException(e);
			}
		}
	}
	
	public void delete(RequestContext request) {
		String[] files = request.getParameters("files");
		String dir = request.getParameter("dir");
		try {
			for (String f : files) {
				String uri = dir + f;
				boolean result = HdfsFileUtils.delete(nameNodeUrl + uri);
				LOG.info("DELETE: " + uri + ", " + result);
			}
		} catch (IOException e) {
			throw new ServiceUnavailableException(e);
		}
	}
	
	public void list(RequestContext request) {
		String dir = request.getParameter("dir");
		try {
			String uri = nameNodeUrl + dir;
			FileSystem fs = FileSystem.get(URI.create(uri), HdfsFileUtils.getConfiguration());
			List<Map<String, String>> list = HdfsFileUtils.get(fs, nameNodeUrl + dir);
			request.setAttribute("list", list);
		} catch (IOException e) {
			throw new ServiceUnavailableException(e);
		}
	}	
}