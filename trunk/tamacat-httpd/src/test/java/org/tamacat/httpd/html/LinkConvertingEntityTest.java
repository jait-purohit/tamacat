/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.html;


import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class LinkConvertingEntityTest {

	private static Pattern pattern = LinkConvertingEntity.CHARSET_PATTERN;

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testUseLinkConvert() {
		String html = "<html><a href=\"test\">TEST</a></html>";
		html = LinkConvertingEntity.convert(html, "test", "zzzz").getData();
		assertEquals("<html><a href=\"zzzz\">TEST</a></html>", html);
	}
	
	@Test
	public void testGetCharset() {
		String html1 = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=utf-8\"></html>";
	
		Matcher matcher = pattern.matcher(html1);
		if (matcher.find()) {
			assertEquals("utf-8", matcher.group(4));
		}
	}
}
