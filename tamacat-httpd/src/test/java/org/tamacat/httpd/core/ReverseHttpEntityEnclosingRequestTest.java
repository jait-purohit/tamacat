/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import static org.junit.Assert.*;


import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceUrl;


public class ReverseHttpEntityEnclosingRequestTest {
	ReverseUrl reverseUrl;
	ServiceUrl url;
	ServerConfig config;
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		ServiceConfig serviceConfig
			= new ServiceConfigXmlParser(config).getServiceConfig();
		url = serviceConfig.getServiceUrl("/test/");
		reverseUrl = url.getReverseUrl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetEntity() {
	}

	@Test
	public void testReverseHttpRequest() throws Exception {
		ReverseHttpEntityEnclosingRequest request =
			new ReverseHttpEntityEnclosingRequest(
					new BasicHttpRequest("GET","/test/test.jsp"),
					reverseUrl);
		request.setEntity(new StringEntity("test"));
		
		assertNotNull(request.getAllHeaders());
		assertEquals("http://localhost:8080/test/test.jsp", request.getRequestLine().getUri());
	}
	
//	@Test
//	public void testClone() throws Exception {
//		ReverseHttpEntityEnclosingRequest request =
//			new ReverseHttpEntityEnclosingRequest(
//					new BasicHttpRequest("GET","/test/test.jsp"),
//					reverseUrl);
//		request.setEntity(new StringEntity("test"));
//		
//		ReverseHttpEntityEnclosingRequest clone = request.clone();
//		assertNotSame(clone, request);
//		assertNotSame(clone.reverseUrl, request.reverseUrl);
//		assertNotSame(clone.getEntity(), request.getEntity());
//	}

}
