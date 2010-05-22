/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.hdfs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.fileupload.FileItem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UnixUserGroupInformation;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.MultipartHttpRequestFilter;
import org.tamacat.util.IOUtils;

public class FileUploadRequestFilter extends MultipartHttpRequestFilter {
	
	@Override
	protected void writeFile(FileItem item, String name) {
		OutputStream out = null;
		Configuration conf = new Configuration();
		conf.set(UnixUserGroupInformation.UGI_PROPERTY_NAME,"hadoop,supergroup");
		
		String uri = getBaseDirectory() + "/" + normalizeFileName(name);
		try {
			FileSystem fs = FileSystem.get(URI.create(uri), conf);
			out = fs.create(new Path(uri));
			InputStream in = new BufferedInputStream(item.getInputStream());
			byte[] fbytes = new byte[1024];
			while ((in.read(fbytes)) >= 0) {
				out.write(fbytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceUnavailableException(e);
		} finally {
			IOUtils.close(out);
		}
	}
}
