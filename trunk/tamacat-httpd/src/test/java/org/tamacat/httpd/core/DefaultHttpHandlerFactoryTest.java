package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigXmlParser;
import org.tamacat.httpd.config.ServiceType;
import org.tamacat.httpd.config.ServiceUrl;

public class DefaultHttpHandlerFactoryTest {

	ServerConfig serverConfig;
	ServiceConfig serviceConfig;
	DefaultHttpHandlerFactory factory;
	
	@Before
	public void setUp() throws Exception {
		serverConfig = new ServerConfig();
		serviceConfig = new ServiceConfigXmlParser(serverConfig).getServiceConfig();
		factory = new DefaultHttpHandlerFactory();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetHttpHandler() {
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		serviceUrl.setHandlerName("DocsHandler");
		serviceUrl.setPath("/docs/");
		serviceUrl.setType(ServiceType.NORMAL);
		
		HttpHandler handler = factory.getHttpHandler(serviceUrl);
		assertEquals(true, ((LocalFileHttpHandler)handler).listings);
		
		handler = factory.getHttpHandler(serviceUrl);
		assertEquals(true, ((LocalFileHttpHandler)handler).listings);
	}
}
