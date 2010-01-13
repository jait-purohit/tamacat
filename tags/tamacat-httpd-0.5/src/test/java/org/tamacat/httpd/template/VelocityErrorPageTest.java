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
import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityErrorPage;
import org.tamacat.util.PropertyUtils;

public class VelocityErrorPageTest {

	@Before
	public void setUp() throws Exception {
		Properties props = PropertyUtils.getProperties("server.properties");
		Velocity.init(props);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPrintErrorPage() {
		VelocityErrorPage template = new VelocityErrorPage();
		HttpRequest request = new BasicHttpRequest("GET", "http://localhost/test");
		HttpResponse response = new BasicHttpResponse(
				new BasicStatusLine(new ProtocolVersion("HTTP",1,1), 404, "Not Found"));
		HttpException exception = new NotFoundException();
		String page = template.getErrorPage(request, response, exception);
		assertNotNull(page);
		System.out.println(page);
	}

}
