/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.junit.Test;

public class HtmlLinkConvertInterceptorTest {

	@Test
	public void testUseLinkConvert() {
		String html = "<html><a href=\"test\">TEST</a></html>";
		html = HtmlLinkConvertInterceptor.LinkConvertingEntity.convert(html, "test", "zzzz");
		assertEquals("<html><a href=\"zzzz\">TEST</a></html>", html);
	}

}
