package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import org.junit.Before;

public class IpAddressMatcherTest {
	final IpAddressMatcher v6matcher = new IpAddressMatcher("fe80::21f:5bff:fe33:bd68");
	final IpAddressMatcher v4matcher = new IpAddressMatcher("192.168.1.104");
	HttpContext ipv4Request = new BasicHttpContext();
	HttpContext ipv6Request = new BasicHttpContext();

	@Before
	public void setup() {
		setRemoteAddress(ipv6Request, "fe80::21f:5bff:fe33:bd68");
		setRemoteAddress(ipv4Request, "192.168.1.104");
	}

	@Test
	public void ipv6MatcherMatchesIpv6Address() {
		assertTrue(v6matcher.matches(ipv6Request));
	}

	@Test
	public void ipv6MatcherDoesntMatchIpv4Address() {
		assertFalse(v6matcher.matches(ipv4Request));
	}

	@Test
	public void ipv4MatcherMatchesIpv4Address() {
		assertTrue(v4matcher.matches(ipv4Request));
	}

	@Test
	public void ipv4SubnetMatchesCorrectly() throws Exception {
		IpAddressMatcher matcher = new IpAddressMatcher("192.168.1.0/24");
		assertTrue(matcher.matches(ipv4Request));
		
		matcher = new IpAddressMatcher("192.168.1.128/25");
		assertFalse(matcher.matches(ipv4Request));
		
		setRemoteAddress(ipv4Request, "192.168.1.159"); // 159=0x9f
		assertTrue(matcher.matches(ipv4Request));
	}

	@Test
	public void ipv6RangeMatches() throws Exception {
		IpAddressMatcher matcher = new IpAddressMatcher("2001:DB8::/48");
		assertTrue(matcher.matches("2001:DB8:0:0:0:0:0:0"));
		assertTrue(matcher.matches("2001:DB8:0:0:0:0:0:1"));
		assertTrue(matcher.matches("2001:DB8:0:FFFF:FFFF:FFFF:FFFF:FFFF"));
		assertFalse(matcher.matches("2001:DB8:1:0:0:0:0:0"));
	}

	// SEC-1733
	@Test
	public void zeroMaskMatchesAnything() throws Exception {
		IpAddressMatcher matcher = new IpAddressMatcher("0.0.0.0/0");
		assertTrue(matcher.matches("123.4.5.6"));
		assertTrue(matcher.matches("192.168.0.159"));
		
		matcher = new IpAddressMatcher("192.168.0.159/0");
		assertTrue(matcher.matches("123.4.5.6"));
		assertTrue(matcher.matches("192.168.0.159"));
	}
	
	static void setRemoteAddress(HttpContext context, String ipAddress) {
		context.setAttribute(RequestUtils.REMOTE_ADDRESS, 
			IpAddressMatcher.parseAddress(ipAddress));
	}
}
