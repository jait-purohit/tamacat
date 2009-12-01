package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class AccessLogUtilsTest {

	private HttpContext context;

	@Before
	public void setUp() throws Exception {
		context = HttpObjectFactory.createHttpContext();
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		context.setAttribute(ExecutionContext.HTTP_REQUEST, request);
		context.setAttribute(ExecutionContext.HTTP_RESPONSE, response);
		
		InetAddress address = InetAddress.getByName("127.0.0.1");
		context.setAttribute(AccessLogUtils.REMOTE_ADDRESS, address);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteAccessLog() {
		long time = System.currentTimeMillis();
		AccessLogUtils.writeAccessLog(context, time);
	}

	@Test
	public void testGetRemoteIPAddress() {
		String ipaddress = AccessLogUtils.getRemoteIPAddress(context);
		assertEquals("127.0.0.1", ipaddress);
	}
}
