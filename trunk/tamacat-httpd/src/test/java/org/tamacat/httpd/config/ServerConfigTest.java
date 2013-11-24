/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerConfigTest {

	ServerConfig config;

	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSocketTimeout() {
		assertEquals(config.getSocketTimeout(), 30000);

		config.setParam("ServerSocketTimeout", "100000");
		assertEquals(config.getSocketTimeout(), 100000);
	}

	@Test
	public void testGetConnectionTimeout() {
		assertEquals(config.getConnectionTimeout(), 30000);

		config.setParam("ConnectionTimeout", "180000");
		assertEquals(config.getConnectionTimeout(), 180000);
	}

	@Test
	public void testGetParamString() {
		assertEquals("url-config.xml", config.getParam("url-config.file"));
		assertEquals(null, config.getParam("none"));
	}

	@Test
	public void testGetParamStringT() {
		assertEquals(80, config.getParam("Port", 9999));
		assertEquals("url-config.xml", config.getParam("url-config.file", ""));

		assertEquals(9999, config.getParam("test", 9999));
		assertEquals(9999L, config.getParam("test", 9999L));
		assertEquals(9999d, config.getParam("test", 9999d));
		assertEquals(9999f, config.getParam("test", 9999f));
		assertEquals('c', config.getParam("test", 'c'));
		assertEquals("9999", config.getParam("test", "9999"));

		assertEquals("80", config.getParam("Port", null));
		assertEquals(null, config.getParam("test", null));
	}
}
