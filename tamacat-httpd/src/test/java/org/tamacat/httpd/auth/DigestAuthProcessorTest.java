package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

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

public class DigestAuthProcessorTest {
	
	private ServerConfig config;
	private DigestAuthProcessor auth;
	private HttpRequest request;
	private HttpResponse response;
	private HttpContext context;
	
	private String authHeader = "Digest username=\"admin\", "
		+ "realm=\"Authentication required 20091124\", "
		+ "nonce=\"bc33508387a84b0eb65ca8dfedb7bcba\", "
		+ "uri=\"/web/\", algorithm=MD5, "
		+ "response=\"97d955134af5546163075400e4727431\", "
		+ "qop=auth, nc=00000001, cnonce=\"8a59d880636c718a\"";
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		auth = new DigestAuthProcessor();
		auth.setRealm("Authentication required 20091124");
		TestAuthComponent authComponent = new TestAuthComponent();
		authComponent.setAuthPassword("pass");
		auth.setAuthComponent(authComponent);
		request = HttpObjectFactory.createHttpRequest("GET", "/web/");
		response = HttpObjectFactory.createHttpResponse("HTTP", 200, "OK");
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
		
		request.setHeader(DigestAuthProcessor.AUTHORIZATION, authHeader);
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
			request.setHeader(DigestAuthProcessor.AUTHORIZATION, authHeader);
			String id = auth.checkUser(request, context);
			assertNotNull(id);
			assertEquals("admin", id);
		} catch (UnauthorizedException e) {
			fail();
		}
	}

	@Test
	public void testGenerateNonce() {
		assertNotNull(auth.generateNonce());
		String nonce1 = auth.generateNonce();
		String nonce2 = auth.generateNonce();
		assertNotSame(nonce1, nonce2);
	}
}
