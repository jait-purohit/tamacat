/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultipartHttpRequestFilterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandleFormField() {
		
	}

	@Test
	public void testHandleFileItem() {
		
	}

	@Test
	public void testWriteFile() {
		
	}

	@Test
	public void testGetBaseDirectory() {
		MultipartHttpRequestFilter filter = new MultipartHttpRequestFilter();
		filter.setBaseDirectory("src/test/resources/");
		assertEquals("src/test/resources/test.txt", filter.getBaseDirectory() + "/" + "test.txt");
	}

}
