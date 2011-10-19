/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;


import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.util.HtmlUtils;

public class HtmlUtilsTest {

	private static Pattern pattern = HtmlUtils.CHARSET_PATTERN;

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testUseLinkConvert_A_HREF() {
		String html = "<html><a href=\"/test/index.html\">TEST</a></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><a href=\"/zzzz/index.html\">TEST</a></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_A_HREF2() {
		String html = "<html><a href='/test/index.html'>TEST</a></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><a href='/zzzz/index.html'>TEST</a></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_A_HREF3() {
		String html = "<html><a href=/test/index.html>TEST</a></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><a href=/zzzz/index.html>TEST</a></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_ACTION() {
		String html = "<html><form action=\"/test/main.do\">TEST</form></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><form action=\"/zzzz/main.do\">TEST</form></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_ACTION2() {
		String html = "<html><form action='/test/main.do'>TEST</form></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><form action='/zzzz/main.do'>TEST</form></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_ACTION3() {
		String html = "<html><form action=/test/main.do>TEST</form></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><form action=/zzzz/main.do>TEST</form></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_IMG_SRC() {
		String html = "<html><img src=\"/test/images/test.jpg\">TEST</form></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><img src=\"/zzzz/images/test.jpg\">TEST</form></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_IMG_SRC2() {
		String html = "<html><img src='/test/images/test.jpg'>TEST</form></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><img src='/zzzz/images/test.jpg'>TEST</form></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_IMG_SRC3() {
		String html = "<html><img src=/test/images/test.jpg>TEST</form></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><img src=/zzzz/images/test.jpg>TEST</form></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_META_URL() {
		String html = "<html><meta http-equiv=\"Refresh\" content=\"0;url=/test/index.html\" /></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><meta http-equiv=\"Refresh\" content=\"0;url=/zzzz/index.html\" /></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_META_URL2() {
		String html = "<html><meta http-equiv='Refresh' content='100;url=/test/index.html' /></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><meta http-equiv='Refresh' content='100;url=/zzzz/index.html' /></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_META_URL3() {
		String html = "<html><meta http-equiv=Refresh content=\"0;url=/test/index.html\" /></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><meta http-equiv=Refresh content=\"0;url=/zzzz/index.html\" /></html>", html);
	}
	
	@Test
	public void testUseLinkConvert_META_URL4() {
		String html = "<html><meta http-equiv=\"Refresh\" content=\"0; url=/test/index.html\" /></html>";
		html = HtmlUtils.convertLink(html, "/test/", "/zzzz/").getData();
		assertEquals("<html><meta http-equiv=\"Refresh\" content=\"0; url=/zzzz/index.html\" /></html>", html);
	}
	
	@Test
	public void testGetCharset() {
		Header header = new BasicHeader("Content-Type", "text/html; charset=UTF-8");
		assertEquals("utf-8", HtmlUtils.getCharSet(header));
	}
	
	@Test
	public void testGetCharsetDefault() {
		Header header = new BasicHeader("Content-Type", "text/html");
		assertEquals(null, HtmlUtils.getCharSet(header));
	}
	
	@Test
	public void testGetCharSetFromMetaTag0() {
		String html1 = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=utf-8\"></html>";
		Matcher matcher = pattern.matcher(html1);
		if (matcher.find()) {
			assertEquals("utf-8", matcher.group(4));
		}
	}
	
	@Test
	public void testGetCharSetFromMetaTag() {
		String html = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\"></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "");
		assertEquals("utf-8", result);
	}
	
	@Test
	public void testGetCharSetFromMetaTag2() {
		String html = "<html><META HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=UTF-8'></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "");
		assertEquals("utf-8", result);
	}
	
	@Test
	public void testGetCharSetFromMetaTag3() {
		String html = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html;charset=UTF-8\"></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "");
		assertEquals("utf-8", result);
	}
	
	@Test
	public void testGetCharSetFromMetaTagDefault() {
		String html = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html\"></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "utf-8");
		assertEquals("utf-8", result);
	}
}
