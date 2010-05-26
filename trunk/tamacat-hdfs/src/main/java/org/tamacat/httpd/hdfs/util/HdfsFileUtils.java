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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UnixUserGroupInformation;
import org.tamacat.util.DateUtils;
import org.tamacat.util.IOUtils;
import org.tamacat.util.StringUtils;

public class HdfsFileUtils {

	public static List<Map<String, String>> get(FileSystem fs, String uri) throws IOException {		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		FileStatus[] files = fs.listStatus(new Path(uri));
		if (files != null) {
			for (FileStatus f : files) {
				Map<String, String> map = new HashMap<String, String>();
				if (f.isDir()) {
					map.put("getName", StringUtils.encode(f.getPath().getName(),"UTF-8") + "/");
					map.put("length", "-");
				} else {
					map.put("getName", StringUtils.encode(f.getPath().getName(), "UTF-8"));
					map.put("length", String.valueOf(f.getLen()));
				}
				map.put("isDirectory", String.valueOf(f.isDir()));
				map.put("lastModified", DateUtils.getTime(new Date(f.getModificationTime()), "yyyy-MM-dd HH:mm"));
				list.add(map);
			}
		}
		return list;
	}
	
	public static Configuration getConfiguration() {
		Configuration conf = new Configuration();
		conf.set(UnixUserGroupInformation.UGI_PROPERTY_NAME,"hadoop,supergroup");
		return conf;
	}
	
	public static void write(InputStream in, String uri) throws IOException {
		write(getConfiguration(), in, uri);
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
	
	public static boolean delete(String uri) throws IOException {
		return delete(getConfiguration(), uri);
	}
	
	public static boolean delete(Configuration conf, String uri) throws IOException {
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		return fs.deleteOnExit(new Path(uri));
	}
}
