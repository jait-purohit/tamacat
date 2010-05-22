/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.hdfs.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UnixUserGroupInformation;
import org.tamacat.util.IOUtils;

public class HdfsAccess_test {

	static String host = "hdfs://localhost";
	
	public static void main(String[] args) throws Exception {
		//create("/user/hadoop/test");
		//read("/user/hadoop/test");
		readDir("/usr/hadoop/html5/data");
	}
	
	static void create(String path) throws Exception {
		String uri = host + path;
		Configuration conf = new Configuration();
		conf.set(UnixUserGroupInformation.UGI_PROPERTY_NAME,"hadoop,supergroup");
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		OutputStream out = fs.create(new Path(uri));
		InputStream in = new FileInputStream(new File("pom.xml"));
        byte[] buffer = new byte[1024];
        long count = 0;
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
            count += n;
        }
        IOUtils.close(in);
        IOUtils.close(out);
	}
	
	static void read(String path) throws Exception {
		String uri = host + path;
		FileSystem fs = FileSystem.get(URI.create(uri),	new Configuration());
		InputStream in = null;
		try {
			in = fs.open(new Path(uri));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String tmp;
	        while((tmp = br.readLine()) != null){
	            System.out.println(tmp);
	        }
		} finally {
	        IOUtils.close(in);
		}
	}

	static void readDir(String path) throws Exception {
		String uri = host + "/user/hadoop/html5/data";
		FileSystem fs = FileSystem.get(URI.create(uri),	new Configuration());
		try {
			FileStatus[] list = fs.listStatus(new Path("/user/hadoop/html5/data"));
			for (FileStatus f : list) {
				System.out.println(f.getPath().getName());
			}
		} finally {
	        IOUtils.close(fs);
		}
	}
}
