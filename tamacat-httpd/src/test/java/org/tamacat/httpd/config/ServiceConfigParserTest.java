/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServiceConfigParserTest {

	ServiceConfigParser parser;
	
	@Before
	public void setUp() throws Exception {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setParam("url-config.file", "url-config.xml");
		parser = new ServiceConfigParser(serverConfig);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetServiceConfig() {
		HostServiceConfig config = parser.getVirtualHostConfig();
		ServiceConfig serviceConfig = config.getDefaultServiceConfig();
		List<ServiceUrl> list = serviceConfig.getServiceUrlList();
		Assert.assertTrue(list.size() > 0);
	}
}
