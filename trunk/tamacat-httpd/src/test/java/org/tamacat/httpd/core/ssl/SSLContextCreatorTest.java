/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.ssl.SSLContextCreator;

public class SSLContextCreatorTest {
	
	@Test
	public void testSSLContextCreatorServerConfig() throws Exception {
		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.keyStoreFile", "test.keystore");
		config.setParam("https.keyPassword", "nopassword");
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.protocol", "SSLv3");
		
		SSLContextCreator creator = new SSLContextCreator(config);
		SSLContext ctx = creator.getSSLContext();
		assertNotNull(ctx);
	}

	@Test
	public void testGetSSLContext() throws Exception {
		SSLContextCreator creator = new SSLContextCreator();
		creator.setKeyStoreFile("test.keystore");
		creator.setKeyPassword("nopassword");
		creator.setKeyStoreType("JKS");
		creator.setSSLProtocol("SSLv3");
		
		SSLContext ctx = creator.getSSLContext();
		assertNotNull(ctx);
	}
}
