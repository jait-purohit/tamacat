package org.tamacat.httpd.filter.geoip;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GeoIPAccessControlFilterTest {

	GeoIPAccessControlFilter filter;
	@Before
	public void setUp() throws Exception {
		filter = new GeoIPAccessControlFilter();
	}

	@Test
	public void testGetCountry() {
		filter.setGeoIpFile("GeoIP.dat");
		assertEquals("AU", filter.getCountry("1.0.0.1"));
		assertEquals("JP", filter.getCountry("1.0.16.1"));
	}
	
	@Test
	public void testIsAccessAllowed() {
		filter.setGeoIpFile("GeoIP.dat");
		filter.setAllowCountry("US,AU");
		assertEquals(true, filter.isAccessAllowed("1.0.0.1"));
		assertEquals(false, filter.isAccessAllowed("1.0.16.1"));
	}
	
	@Test
	public void testIsAccessAllowed2() {
		filter.setGeoIpFile("GeoIP.dat");
		filter.setAllowCountry("US,AU");
		assertEquals(true, filter.isAccessAllowed("10.0.0.1"));
		assertEquals(true, filter.isAccessAllowed("172.16.0.1"));
		assertEquals(true, filter.isAccessAllowed("192.168.0.1"));
		assertEquals(true, filter.isAccessAllowed("127.0.0.1"));
		assertEquals(true, filter.isAccessAllowed("100.64.0.1"));
		assertEquals(true, filter.isAccessAllowed("100.127.255.254"));
	}
	
	@Test
	public void testIsAccessAllowed3() {
		filter.setGeoIpFile("GeoIP.dat");
		filter.setAllowCountry("US,AU");
		filter.setAllowPrivateIpAddress(false);
		assertEquals(false, filter.isAccessAllowed("10.0.0.1"));
		assertEquals(false, filter.isAccessAllowed("172.16.0.1"));
		assertEquals(false, filter.isAccessAllowed("192.168.0.1"));
		assertEquals(false, filter.isAccessAllowed("127.0.0.1"));
		assertEquals(false, filter.isAccessAllowed("100.64.0.1"));
		assertEquals(false, filter.isAccessAllowed("100.127.255.254"));
	}
}
