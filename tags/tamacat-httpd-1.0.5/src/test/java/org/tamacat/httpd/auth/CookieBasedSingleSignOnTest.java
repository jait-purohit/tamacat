package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class CookieBasedSingleSignOnTest {

	@Test
	public void testCookieBasedSingleSignOnString() {
		assertEquals("User", new CookieBasedSingleSignOn("User").singleSignOnCookieName);
	}

	@Test
	public void testCookieBasedSingleSignOn() {
		assertEquals("SingleSignOnUser", new CookieBasedSingleSignOn().singleSignOnCookieName);
	}

	@Test
	public void testSetRemoteUserKey() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		sso.setRemoteUserKey("RemoteUser");
		assertEquals("RemoteUser", sso.remoteUserKey);
	}

	@Test
	public void testSetSingleSignOnCookieName() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		sso.setSingleSignOnCookieName("User");
		assertEquals("User", sso.singleSignOnCookieName);
	}

	@Test
	public void testIsFreeAccessExtensions() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		sso.setFreeAccessExtensions(".gif,.jpg");
		assertEquals(true, sso.isFreeAccessExtensions("/test.gif"));
		assertEquals(true, sso.isFreeAccessExtensions("/test.jpg"));
		assertEquals(false, sso.isFreeAccessExtensions("/test.html"));
	}

	@Test
	public void testGetSignedUser() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		HttpRequest request = new BasicHttpRequest("GET", "");
		HttpContext context = new BasicHttpContext();
		context.setAttribute(sso.remoteUserKey, "admin");
		assertEquals("admin", sso.getSignedUser(request, context));
	}
	
	@Test
	public void testIsSigned() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		HttpRequest request = new BasicHttpRequest("GET", "");
		HttpContext context = new BasicHttpContext();
		context.setAttribute(sso.remoteUserKey, "admin");
		assertEquals(true, sso.isSigned(request, context));
	}

	@Test
	public void testSign() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		HttpRequest request = new BasicHttpRequest("GET", "");
		HttpContext context = new BasicHttpContext();
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		try {
			assertEquals(false, sso.isSigned(request, context));
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
		sso.sign("admin", request, response, context);
		context.setAttribute(sso.remoteUserKey, "admin");
		assertEquals(true, sso.isSigned(request, context));
		
		assertEquals("admin", sso.getSignedUser(request, context));
	}
	
	@Test
	public void testSign2() {
		CookieBasedSingleSignOn sso = new CookieBasedSingleSignOn();
		HttpRequest request = new BasicHttpRequest("GET", "");
		HttpContext context = new BasicHttpContext();
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");

		sso.sign("admin", request, response, context);
		assertEquals(true, sso.isSigned(request, context));
		assertEquals("admin", sso.getSignedUser(request, context));
	}
}
