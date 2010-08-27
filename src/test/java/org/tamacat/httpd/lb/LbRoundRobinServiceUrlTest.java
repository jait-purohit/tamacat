/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.lb;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.monitor.MonitorConfig;

public class LbRoundRobinServiceUrlTest {

	ServerConfig serverConfig;
	
	@Before
	public void setUp() throws Exception {
		serverConfig = new ServerConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMonitorConfigDefault() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		serviceUrl.setPath("/");
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://localhost:8080/"));
		MonitorConfig config = url.getMonitorConfig(reverseUrl);
		assertNotNull(config);
		assertEquals("http://localhost:8080/check.html", config.getUrl());
		assertEquals(30000, config.getInterval());
		assertEquals(10000, config.getTimeout());
	}
	
	@Test
	public void testGetMonitorConfig() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		serviceUrl.setPath("/lb/");

		reverseUrl.setReverse(new URL("http://localhost:8080/lb1/"));
		MonitorConfig config = url.getMonitorConfig(reverseUrl);
		assertNotNull(config);
		assertEquals("http://localhost:8080/lb1/test/check.html", config.getUrl());
		assertEquals(60000, config.getInterval());
		assertEquals(15000, config.getTimeout());
	}
}
