/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionManagerTest {

	@Before
	public void setUp() throws Exception {
		SessionManager.setDefaultMaxInactiveInterval(10000);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetSession() throws Exception {
		Session session = SessionManager.getInstance().createSession();
		session.setMaxInactiveInterval(1000);
		String id = session.getId();
		System.out.println(id);
		
		//Thread.sleep(5000);
		
		Session session2 = SessionManager.getInstance().getSession(id);
		System.out.print(session2 != null ? session2.getId() : null);
	}

}
