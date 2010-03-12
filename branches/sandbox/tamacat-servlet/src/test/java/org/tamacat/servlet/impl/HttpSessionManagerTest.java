package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;

public class HttpSessionManagerTest {
	HttpSessionManager manager;
	
	@Before
	public void setUp() throws Exception {
		ServiceConfigParser parser = new ServiceConfigParser(new ServerConfig());
		ServiceUrl serviceUrl = parser.getConfig().getDefaultServiceConfig().getServiceUrl("/test/");
		manager = new HttpSessionManager(new ServletContextImpl("test", serviceUrl));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateSession() {
		HttpSession session = manager.createSession();
		assertNotNull(session);
		assertNotNull(session.getId());
	}
	
	@Test
	public void testGetSession() {
		HttpSession session = manager.getSession(null, false);
		assertNull(session);
		
		session = manager.getSession(null, true);
		assertNotNull(session);
	}

	@Test
	public void testInvalidate() {
		HttpSession session = manager.createSession();
		String id = session.getId();
		HttpSessionManager.invalidate(session);
		
		HttpSession session2 = manager.getSession(id, false);
		assertNull(session2);
	}

}
