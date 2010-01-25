package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServletUrlTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetServletPath() {
		ServletUrl servletUrl = new ServletUrl();
		servletUrl.setUrlPattern("index.html");
		assertEquals("index.html", servletUrl.getServletPath("/index.html/test"));
		
		servletUrl.setUrlPattern("/servlet/Main");
		assertEquals("/servlet/Main", servletUrl.getServletPath("/servlet/Main?id=test"));
		
		servletUrl.setUrlPattern("/*.do");
		assertEquals("/main.do", servletUrl.getServletPath("/main.do?id=test"));
	}

}
