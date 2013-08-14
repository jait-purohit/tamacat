package org.tamacat.httpd.core.jmx;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.core.jmx.BasicCounter;

public class BasicCounterTest {
	
	BasicCounter counter;
	
	@Before
	public void setUp() throws Exception {
		counter = new BasicCounter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetPath() {
		counter.setPath("test");
		assertEquals("test", counter.getPath());
	}

	@Test
	public void testGetActiveConnections() {
		assertEquals(0, counter.getActiveConnections());
		counter.countUp();
		assertEquals(1, counter.getActiveConnections());
		counter.countDown();
		assertEquals(0, counter.getActiveConnections());
	}

	@Test
	public void testGetAccessCount() {
		assertEquals(0, counter.getAccessCount());
		counter.access();
		assertEquals(1, counter.getAccessCount());
		counter.access();
		assertEquals(2, counter.getAccessCount());
		counter.access();
		assertEquals(3, counter.getAccessCount());
		counter.resetAccessCount();
		assertEquals(0, counter.getAccessCount());
	}

	@Test
	public void testGetErrorCount() {
		assertEquals(0, counter.getErrorCount());
		counter.error();
		assertEquals(1, counter.getErrorCount());
		counter.error();
		assertEquals(2, counter.getErrorCount());
		counter.error();
		assertEquals(3, counter.getErrorCount());
		counter.resetErrorCount();
		assertEquals(0, counter.getErrorCount());
	}

	@Test
	public void testGetStartedTime() {
		assertNotNull(counter.getStartedTime());
	}

	@Test
	public void testGetAverageResponseTime() {
		assertEquals(0, counter.getAverageResponseTime());

		counter.setResponseTime(1000);
		counter.setResponseTime(2000);
		counter.setResponseTime(3000);
		assertEquals(2000, counter.getAverageResponseTime());
	}

	@Test
	public void testGetMaximumResponseTime() {
		assertEquals(0, counter.getMaximumResponseTime());

		counter.setResponseTime(1000);
		counter.setResponseTime(2000);
		counter.setResponseTime(3000);
		assertEquals(3000, counter.getMaximumResponseTime());
	}

	@Test
	public void testRegister() {
		//counter.register();
	}
}
