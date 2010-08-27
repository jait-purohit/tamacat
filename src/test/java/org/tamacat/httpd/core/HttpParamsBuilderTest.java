/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpParamsBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildDefault() {
		HttpParamsBuilder builder = new HttpParamsBuilder();
		HttpParams params = builder.buildParams();
		assertEquals(5000, params.getParameter(CoreConnectionPNames.SO_TIMEOUT));
		assertEquals((8*1024), params.getParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE));
		assertEquals(false, params.getParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK));
		assertEquals(true, params.getParameter(CoreConnectionPNames.TCP_NODELAY));
		//assertEquals("tamacat-httpd-0.4", params.getParameter(CoreProtocolPNames.ORIGIN_SERVER));
	}

	@Test
	public void testBuild() {
		HttpParamsBuilder builder = new HttpParamsBuilder();
		builder.socketTimeout(3000);
		builder.connectionTimeout(10000);
		builder.socketBufferSize((4*1024));
		builder.staleConnectionCheck(true);
		builder.tcpNoDelay(false);
		builder.originServer("TEST/1.1");
		
		HttpParams params = builder.buildParams();
		assertEquals(3000, params.getParameter(CoreConnectionPNames.SO_TIMEOUT));
		assertEquals(10000, params.getParameter(CoreConnectionPNames.CONNECTION_TIMEOUT));
		assertEquals((4*1024), params.getParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE));
		assertEquals(true, params.getParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK));
		assertEquals(false, params.getParameter(CoreConnectionPNames.TCP_NODELAY));
		assertEquals("TEST/1.1", params.getParameter(CoreProtocolPNames.ORIGIN_SERVER));
	}
}
