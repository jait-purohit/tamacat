package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class VelocityHttpHandlerTest {
		
	@Test
	public void testIsMatchUrlPattern_default() {
		VelocityHttpHandler handler = new VelocityHttpHandler();
		assertTrue(handler.isMatchUrlPattern("/test.html"));
		assertTrue(handler.isMatchUrlPattern("/ctl/test.html"));
		assertFalse(handler.isMatchUrlPattern("/ctl/"));
	}

	@Test
	public void testIsMatchUrlPattern_single() {
		VelocityHttpHandler handler = new VelocityHttpHandler();
		handler.setUrlPatterns(".do");
		assertFalse(handler.isMatchUrlPattern("/test.html"));
		assertFalse(handler.isMatchUrlPattern("/ctl/test.html"));
		assertFalse(handler.isMatchUrlPattern("/ctl/"));
		assertTrue(handler.isMatchUrlPattern("/test.do"));
	}
	
	@Test
	public void testIsMatchUrlPattern_multi() {
		VelocityHttpHandler handler = new VelocityHttpHandler();
		handler.setUrlPatterns("/ctl/, .do");
		assertFalse(handler.isMatchUrlPattern("/test.html"));
		assertTrue(handler.isMatchUrlPattern("/ctl/test.html"));
		assertTrue(handler.isMatchUrlPattern("/ctl/"));
		assertTrue(handler.isMatchUrlPattern("/test.do"));
	}
}
