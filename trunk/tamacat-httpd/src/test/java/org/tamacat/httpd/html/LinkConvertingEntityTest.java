/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.html;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LinkConvertingEntityTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteToOutputStream() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaa/test.html\">aaa</a></html>");
		String before = "/aaa/";
		String after = "/bbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals("<html><a href=\"/bbb/test.html\">aaa</a></html>", new String(out.toByteArray()));
	}
}
