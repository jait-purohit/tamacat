package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
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
	
	@Test
	public void testGetRequestHost() throws Exception {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");
		
		URL url = RequestUtils.getRequestURL(request, null);
		assertNull(url);

		request.setHeader(HTTP.TARGET_HOST, "example.com");
		url = RequestUtils.getRequestURL(request, null);
		assertEquals("http://example.com/test.html", url.toString());
		
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "8080");
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestURL(request, null, serviceUrl);
		assertEquals("http://example.com:8080/test.html", url.toString());
		
		serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "443");
		serverConfig.setParam("https", "true");
		serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestURL(request, null, serviceUrl);
		assertEquals("https://example.com/test.html", url.toString());
	}
	
	@Test
	public void testGetRequestHostURL() {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");
		
		String url = RequestUtils.getRequestHostURL(request, null, null);
		assertNull(url);

		request.setHeader(HTTP.TARGET_HOST, "example.com");
		url = RequestUtils.getRequestHostURL(request, null, null);
		assertEquals("http://example.com", url);
		
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "8080");
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestHostURL(request, null, serviceUrl);
		assertEquals("http://example.com:8080", url.toString());
		
		serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "443");
		serverConfig.setParam("https", "true");
		serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestHostURL(request, null, serviceUrl);
		assertEquals("https://example.com", url.toString());		
	}
}
