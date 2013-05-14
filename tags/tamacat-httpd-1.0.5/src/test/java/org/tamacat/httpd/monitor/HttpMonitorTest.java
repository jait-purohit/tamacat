package org.tamacat.httpd.monitor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpMonitorTest {
	HttpMonitor<String> monitor;
	
	@Before
	public void setUp() throws Exception {
		monitor = new HttpMonitor<String>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetHealthCheckTarget() {
		MonitorEventImpl event = new MonitorEventImpl();
		monitor.setHealthCheckTarget(event);
	}

	@Test
	public void testSetTarget() {
		monitor.setTarget("");
	}

	@Test
	public void testSetMonitorConfig() {
		MonitorConfig config = new MonitorConfig();
		monitor.setMonitorConfig(config);
	}

	@Test
	public void testCheck() {
		assertTrue(monitor.check());
	}

	@Test
	public void testIsNormal() {
		assertTrue(monitor.isNormal());
	}

	@Test
	public void testStartMonitor() {
		monitor.startMonitor();
		monitor.stopMonitor();
	}
	
	static class MonitorEventImpl implements MonitorEvent<String> {
		@Override
		public void removeTarget(String target) {
			//
		}

		@Override
		public void addTarget(String target) {
			
		}
	}

}
