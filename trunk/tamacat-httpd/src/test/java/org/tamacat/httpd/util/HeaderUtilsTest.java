/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HTTP;
import org.junit.Test;
import org.tamacat.httpd.util.HeaderUtils;

public class HeaderUtilsTest {

	@Test
	public void testHeaderUtils() {
		new HeaderUtils();
	}
	
	@Test
	public void testGetHeader() {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");
		request.addHeader("id", "test");
		request.addHeader("test", "");
		assertEquals("test", HeaderUtils.getHeader(request, "id"));
		assertEquals("", HeaderUtils.getHeader(request, "test"));
		assertEquals(null, HeaderUtils.getHeader(request, "abc"));
		
		assertEquals("default", HeaderUtils.getHeader(request, "abc", "default"));
	}
	
	@Test
	public void testEqualsName() {
		assertTrue(HeaderUtils.equalsName(new BasicHeader("name", "abc"), new BasicHeader("name", "abc")));
		assertTrue(HeaderUtils.equalsName(new BasicHeader("NAME", "abc"), new BasicHeader("name", "abc")));
		assertFalse(HeaderUtils.equalsName(new BasicHeader("test", "abc"), new BasicHeader("name", "abc")));
		
		assertFalse(HeaderUtils.equalsName(new BasicHeader("name", "abc"), null));
		assertFalse(HeaderUtils.equalsName(null, new BasicHeader("name", "abc")));
		assertFalse(HeaderUtils.equalsName(null, null));
	}
	
	@Test
	public void testGetCookiesString() {
		String value = "id=guest; session=1234567890; none=; name=test;";
		List<Cookie> cookies = HeaderUtils.getCookies(value);
		
		assertEquals(4, cookies.size());
		
		assertEquals("id", cookies.get(0).getName());
		assertEquals("guest", cookies.get(0).getValue());
		
		assertEquals("session", cookies.get(1).getName());
		assertEquals("1234567890", cookies.get(1).getValue());
		
		assertEquals("none", cookies.get(2).getName());
		assertEquals("", cookies.get(2).getValue());
		
		assertEquals("name", cookies.get(3).getName());
		assertEquals("test", cookies.get(3).getValue());
	}
	
	
	@Test
	public void testGetCookieValue() {
		String value = "id=guest; session=1234567890; none=; name=test;";
		Header header = new BasicHeader("Cookie", value);
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");
		request.setHeader(header);
		
		assertEquals("guest", HeaderUtils.getCookieValue(request, "id"));
		assertEquals("1234567890", HeaderUtils.getCookieValue(request, "session"));
		assertEquals("", HeaderUtils.getCookieValue(request, "none"));
		assertEquals("test", HeaderUtils.getCookieValue(request, "name"));
		assertEquals(null, HeaderUtils.getCookieValue(request, "abc"));
	}
	
	@Test
	public void testGetCookieValueStringString() {
		String cookie = "id=guest; session=1234567890; none=; name=test;";
		assertEquals("guest", HeaderUtils.getCookieValue(cookie, "id"));
		assertEquals("1234567890", HeaderUtils.getCookieValue(cookie, "session"));
		assertEquals("", HeaderUtils.getCookieValue(cookie, "none"));
		assertEquals("test", HeaderUtils.getCookieValue(cookie, "name"));
		assertEquals(null, HeaderUtils.getCookieValue(cookie, "abc"));
	}
	
	@Test
	public void testInContentType() {
	    Set<String> contentTypes = new HashSet<String>();
		contentTypes.add("html");
		
		assertEquals(false, HeaderUtils.inContentType(contentTypes, null));
		
		Header header1 = new BasicHeader(HTTP.CONTENT_TYPE, "text/html");
		assertEquals(true, HeaderUtils.inContentType(contentTypes, header1));
		
		Header header2 = new BasicHeader(HTTP.CONTENT_TYPE, "text/xml");
		assertEquals(false, HeaderUtils.inContentType(contentTypes, header2));
		
		contentTypes.add("image/jpeg");
		Header header3 = new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg");
		assertEquals(true, HeaderUtils.inContentType(contentTypes, header3));
		
		Header header4 = new BasicHeader(HTTP.CONTENT_TYPE, "text/html; charset=UTF-8");
		assertEquals(true, HeaderUtils.inContentType(contentTypes, header4));
		Header header5 = new BasicHeader(HTTP.CONTENT_TYPE, "text/html;");
		assertEquals(true, HeaderUtils.inContentType(contentTypes, header5));
		Header header6 = new BasicHeader(HTTP.CONTENT_TYPE, null);
		assertEquals(false, HeaderUtils.inContentType(contentTypes, header6));
	}
	
	@Test
	public void testIsMultipart() {
		assertTrue(HeaderUtils.isMultipart("multipart/form-data"));
		assertFalse(HeaderUtils.isMultipart("application/x-www-form-urlencoded"));
		assertFalse(HeaderUtils.isMultipart(null));
	}
}