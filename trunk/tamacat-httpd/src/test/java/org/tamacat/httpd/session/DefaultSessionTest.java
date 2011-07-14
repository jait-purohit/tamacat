package org.tamacat.httpd.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultSessionTest {

	DefaultSession session;
	
	@Before
	public void setUp() throws Exception {
		session = new DefaultSession();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefaultSessionStringDateDateBoolean() {
		
	}

	@Test
	public void testDefaultSession() {
		session = new DefaultSession();
		//assertEquals(30*60*1000, session.getMaxInactiveInterval());
	}

	@Test
	public void testDefaultSessionInt() {
		session = new DefaultSession(1000);
	}

	@Test
	public void testGetAttribute() {
		
	}

	@Test
	public void testGetAttributeKeys() {
		
	}

	@Test
	public void testGetSessionAttributes() {
		
	}

	@Test
	public void testGetCreationDate() {
		assertNotNull(session.getCreationDate());
	}

	@Test
	public void testGetLastAccessDate() {
		assertNotNull(session.getLastAccessDate());
	}

	@Test
	public void testGetId() {
		assertNotNull(session.getId());
	}

	@Test
	public void testInvalidate() {
		session.invalidate();
	}

	@Test
	public void testGetMaxInactiveInterval() {
		session.getMaxInactiveInterval();
	}

	@Test
	public void testUpdateSession() {
		session.updateSession();
	}

	@Test
	public void testSetSessionStore() {
		session.setSessionStore(new FileSessionStore());
	}

}
