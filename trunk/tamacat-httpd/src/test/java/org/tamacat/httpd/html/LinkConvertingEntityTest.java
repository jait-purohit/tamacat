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
		System.out.println(new String(out.toByteArray()));

		assertEquals(html.getContentLength(), entity.getContentLength());
		assertEquals("<html><a href=\"/bbb/test.html\">aaa</a></html>", new String(out.toByteArray()));
	}
	
	@Test
	public void testWriteToOutputStream2() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaaaa/test.html\">aaa</a></html>");
		String before = "/aaaaa/";
		String after = "/bbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals(html.getContentLength()-2, entity.getContentLength());
		assertEquals("<html><a href=\"/bbb/test.html\">aaa</a></html>", new String(out.toByteArray()));
	}
	
	@Test
	public void testWriteToOutputStream3() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaa/test.html\">aaa</a></html>");
		String before = "/aaa/";
		String after = "/bbbbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals(html.getContentLength()+2, entity.getContentLength());
		assertEquals("<html><a href=\"/bbbbb/test.html\">aaa</a></html>", new String(out.toByteArray()));
	}
	
	
	@Test
	public void testWriteToOutputStream_NAME() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaa/test.html\">/aaa/</a></html>");
		String before = "/aaa/";
		String after = "/bbbbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals(html.getContentLength()+2, entity.getContentLength());
		assertEquals("<html><a href=\"/bbbbb/test.html\">/aaa/</a></html>", new String(out.toByteArray()));
	}
	
	@Test
	public void testWriteToOutputStream_ERROR() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/test.html\">/aaaaa/</a></html>");
		String before = "/aaaaa/";
		String after = "/bbbbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after);
		assertNotNull(entity);
		
		try {
			entity.writeTo(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Output stream may not be null", e.getMessage());
		}
	}
}
