package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class RequestUtilsTest {

	private HttpContext context;

	@Before
	public void setUp() throws Exception {
		context = HttpObjectFactory.createHttpContext();
		InetAddress address = InetAddress.getByName("127.0.0.1");
		context.setAttribute(RequestUtils.REMOTE_ADDRESS, address);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetRemoteIPAddress() {
		String ipaddress = RequestUtils.getRemoteIPAddress(context);
		assertEquals("127.0.0.1", ipaddress);
	}
}
