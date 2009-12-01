package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class BasicAuthProcessorTest {

	ServerConfig config;
	BasicAuthProcessor auth;
	HttpRequest request;
	HttpResponse response;
	HttpContext context;
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		auth = new BasicAuthProcessor();
		TestAuthComponent authComponent = new TestAuthComponent();
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
	public void testDoFilter() {
		ServiceUrl serviceUrl = new ServiceUrl(config);
		try {
			auth.doFilter(request, response, context, serviceUrl);
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
		
		String idpass = new String(new Base64().encode("admin:pass".getBytes()));
		request.setHeader(BasicAuthProcessor.AUTHORIZATION, "Basic " + idpass);
		try {
			auth.doFilter(request, response, context, serviceUrl);
		} catch (UnauthorizedException e) {
			fail();
		}
	}

	@Test
	public void testCheckUser() {
		try {
			String id = auth.checkUser(request, context);
			assertNull(id);
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
		try {
			String idpass = new String(new Base64().encode("admin:pass".getBytes()));
			request.setHeader(BasicAuthProcessor.AUTHORIZATION, "Basic " + idpass);
			String id = auth.checkUser(request, context);
			assertNotNull(id);
			assertEquals("admin", id);
		} catch (UnauthorizedException e) {
			fail();
		}
	}
}
