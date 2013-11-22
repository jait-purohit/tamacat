package org.tamacat.httpd.filter.geoip;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GeoIP2AccessControlFilterTest {

	GeoIP2AccessControlFilter filter;
	@Before
	public void setUp() throws Exception {
		filter = new GeoIP2AccessControlFilter();
	}

	@Test
	public void testGetCountry() throws Exception {
		filter.setGeoIpFile("GeoLite2-Country.mmdb");
		assertEquals("AU", filter.getCountry("1.0.0.1"));
		assertEquals("JP", filter.getCountry("1.0.16.1"));
		try {
			assertEquals("US", filter.getCountry("2001:4860:4860::8888"));
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testIsAccessAllowed() {
		filter.setGeoIpFile("GeoLite2-Country.mmdb");
		filter.setAllowCountry("US,AU");
		assertEquals(true, filter.isAccessAllowed("1.0.0.1"));
		assertEquals(false, filter.isAccessAllowed("1.0.16.1"));
	}
	
	@Test
	public void testIsAccessAllowed2() {
		filter.setGeoIpFile("GeoLite2-Country.mmdb");
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
		filter.setGeoIpFile("GeoLite2-Country.mmdb");
		filter.setAllowCountry("US,AU");
		filter.setAllowPrivateIpAddress(false);
		assertEquals(false, filter.isAccessAllowed("10.0.0.1"));
		assertEquals(false, filter.isAccessAllowed("172.16.0.1"));
		assertEquals(false, filter.isAccessAllowed("192.168.0.1"));
		assertEquals(false, filter.isAccessAllowed("127.0.0.1"));
		assertEquals(false, filter.isAccessAllowed("100.64.0.1"));
		assertEquals(false, filter.isAccessAllowed("100.127.255.254"));
	}
	
	@Test
	public void testIsAccessAllowedIPv6() {
		filter.setGeoIpFile("GeoLite2-Country.mmdb");
		filter.setAllowCountry("US,AU");
		assertEquals(true, filter.isAccessAllowed("2001:4860:4860::8888"));
		//assertEquals(true, filter.isAccessAllowed("2001:0db8:bd05:01d2:288a:1fc0:0001:10ee"));

	}
}
