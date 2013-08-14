/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.template;

import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.handler.page.VelocityErrorPage;
import org.tamacat.util.PropertyUtils;

public class VelocityErrorPageTest {
	
	private Properties props;

	@Before
	public void setUp() throws Exception {
		props = PropertyUtils.getProperties("server.properties", 
				  getClass().getClassLoader());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPrintErrorPage() {
		VelocityErrorPage template = new VelocityErrorPage(props);
		HttpRequest request = new BasicHttpRequest("GET", "http://localhost/test");
		HttpResponse response = new BasicHttpResponse(
				new BasicStatusLine(new ProtocolVersion("HTTP",1,1), 404, "Not Found"));
		HttpException exception = new NotFoundException();
		String page = template.getErrorPage(request, response, exception);
		assertNotNull(page);
	}
}
