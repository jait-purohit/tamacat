/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.hdfs.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UnixUserGroupInformation;
import org.tamacat.util.IOUtils;

public class HdfsFileUtils {

	public static void write(InputStream in, String uri) throws IOException {
		Configuration conf = new Configuration();
		conf.set(UnixUserGroupInformation.UGI_PROPERTY_NAME,"hadoop,supergroup");
		write(conf, in, uri);
	}
	
	public static void write(Configuration conf, InputStream in, String uri) throws IOException {		
		OutputStream out = null;
		try {
			FileSystem fs = FileSystem.get(URI.create(uri), conf);
			out = fs.create(new Path(uri));
			InputStream is = new BufferedInputStream(in);
			byte[] fbytes = new byte[8192];
			while ((is.read(fbytes)) >= 0) {
				out.write(fbytes);
			}
		} finally {
			IOUtils.close(out);
		}
	}
}
