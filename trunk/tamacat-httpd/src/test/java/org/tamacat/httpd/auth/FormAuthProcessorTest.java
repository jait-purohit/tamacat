/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.httpd.util.RequestUtils;

public class FormAuthProcessorTest {

	ServerConfig config;
	FormAuthProcessor auth;
	HttpRequest request;
	HttpResponse response;
	HttpContext context;
	TestAuthComponent authComponent;
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		auth = new FormAuthProcessor();
		auth.init(serviceUrl);
		
		authComponent = new TestAuthComponent();
		authComponent.setAuthPassword("pass");
		auth.setAuthComponent(authComponent);
		request = HttpObjectFactory.createHttpRequest("GET", "/");
		response = HttpObjectFactory.createHttpResponse(200, "OK");
		context = new BasicHttpContext();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsFreeAccessExtensions() {
		auth.setFreeAccessExtensions(".css, .js, .gif");
		
		assertTrue(auth.isFreeAccessExtensions("/test/test.css"));
		assertTrue(auth.isFreeAccessExtensions("/test/test.js"));
		assertTrue(auth.isFreeAccessExtensions("/test/test.gif"));
		
		assertFalse(auth.isFreeAccessExtensions("/test/"));
		assertFalse(auth.isFreeAccessExtensions("."));
		assertFalse(auth.isFreeAccessExtensions("/"));
		assertFalse(auth.isFreeAccessExtensions(""));
		assertFalse(auth.isFreeAccessExtensions("/test/test.png"));
		assertFalse(auth.isFreeAccessExtensions("/test/test.jsp"));
	}

	@Test
	public void testSendRedirect() throws Exception {
		auth.sendRedirect(response, "/test/login.html");
		assertEquals(
			"<html><meta http-equiv=\"refresh\" content=\"0;url=/test/login.html\"></html>",
			EntityUtils.toString(response.getEntity())
		);
	}

	@Test
	public void testCheckUser() throws Exception {
		try {
			auth.checkUser(request, context);
			fail();
		} catch (UnauthorizedException e) {
			assertEquals(BasicHttpStatus.SC_UNAUTHORIZED, e.getHttpStatus());
		}
		//login
		RequestUtils.setParameter(context, "username", "admin");
		RequestUtils.setParameter(context, "password", "pass");
		auth.checkUser(request, context);
	}

}
