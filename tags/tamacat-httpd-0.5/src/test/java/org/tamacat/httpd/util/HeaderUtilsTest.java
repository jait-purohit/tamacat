/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.junit.Test;
import org.tamacat.httpd.util.HeaderUtils;

public class HeaderUtilsTest {

	@Test
	public void testInContentType() {
	    Set<String> contentTypes = new HashSet<String>();
		contentTypes.add("html");
		
		Header header1 = new BasicHeader(HTTP.CONTENT_TYPE, "text/html");
		assertEquals(true, HeaderUtils.inContentType(contentTypes, header1));
		
		Header header2 = new BasicHeader(HTTP.CONTENT_TYPE, "text/xml");
		assertEquals(false, HeaderUtils.inContentType(contentTypes, header2));
	}
}