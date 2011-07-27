/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;

public class MultipartHttpRequestFilterTest {
	HttpContext context;
	FileItem item;
	MultipartHttpFilter filter;
	
	@Before
	public void setUp() throws Exception {
		filter = new MultipartHttpFilter();
		filter.setBaseDirectory("src/test/resources/");
		filter.setAlgorithm("SHA-256");
		filter.setFileSizeMax(10000);
		filter.setEncoding("UTF-8");
		filter.setWriteFile(false);
		
		context = new BasicHttpContext();
		item = new DiskFileItemFactory().createItem("test", "text/plain", false, "test.txt");
		this.item.getOutputStream();
		
		ServerConfig config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		filter.init(serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandleFormField() {
		filter.handleFormField(context, item);
	}

	@Test
	public void testHandleFileItem() {
		filter.handleFileItem(context, item);
	}

	@Test
	public void testWriteFile() throws IOException {
		filter.writeFile(item, "test.txt");
	}

	@Test
	public void testGetBaseDirectory() {
		assertEquals("src/test/resources/test.txt", filter.getBaseDirectory() + "/" + "test.txt");
	}

}
