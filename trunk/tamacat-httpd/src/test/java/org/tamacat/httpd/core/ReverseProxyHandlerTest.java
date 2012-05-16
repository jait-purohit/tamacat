package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceType;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.PropertyUtils;

public class ReverseProxyHandlerTest {

	ReverseProxyHandler handler;
	
	@Before
	public void setUp() throws Exception {
		handler = new ReverseProxyHandler();

		ServerConfig serverConfig = new ServerConfig(PropertyUtils.getProperties("server.properties"));
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		
		serviceUrl.setPath("/test/");
		serviceUrl.setType(ServiceType.REVERSE);
		serviceUrl.setHost(new URL("http://localhost/test/"));		
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://localhost:8080/examples/"));
		serviceUrl.setReverseUrl(reverseUrl);
		
		handler.setServiceUrl(serviceUrl);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	HttpContext createContext() {
		HttpContext context = HttpObjectFactory.createHttpContext();
		try {
			InetAddress address = InetAddress.getByName("127.0.0.1");
			context.setAttribute(RequestUtils.REMOTE_ADDRESS, address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return context;
	}
	
	@Test
	public void testHandle() {
		HttpRequest request = new BasicHttpRequest("GET", "/test/test.html");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = createContext();
				
		handler.setHttpFilter(new RequestFilter() {
			@Override
			public void init(ServiceUrl serviceUrl) {
			}
			@Override
			public void doFilter(HttpRequest request, HttpResponse response,
					HttpContext context) {				
			}
		});
		handler.handle(request, response, context);

		handler.setHttpFilter(new ResponseFilter() {
			@Override
			public void init(ServiceUrl serviceUrl) {
			}
			@Override
			public void afterResponse(HttpRequest request, HttpResponse response,
					HttpContext context) {				
			}
		});
		handler.handle(request, response, context);
	}

	@Test
	public void testDoRequest() throws HttpException, IOException {
		HttpRequest request = new BasicHttpRequest("GET", "/test/test.html");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = createContext();
		
		handler.doRequest(request, response, context);
	}

	@Test
	public void testGetEntity() {
		assertNotNull(handler.getEntity("<html>TEST</html>"));
	}

	@Test
	public void testGetFileEntity() {
		assertNotNull(handler.getFileEntity(new File("./src/test/resources/htdocs/index.html")));
	}

	@Test
	public void testForwardRequest() {
		HttpRequest request = new BasicHttpRequest("GET", "/test/test.html");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = createContext();
		handler.forwardRequest(request, response, context);
	}
	
	@Test
	public void testInfiniteLoop() {
		HttpRequest request = new BasicHttpRequest("GET", "/test/test.html");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = createContext();

		handler.forwardRequest(request, response, context);
		try {
			handler.forwardRequest(request, response, context);
			fail();
		} catch (ServiceUnavailableException e) {
			assertEquals("reverseUrl is infinite loop.", e.getMessage());
		}
	}

	@Test
	public void testSetDefaultHttpRequestInterceptor() {
		handler.setDefaultHttpRequestInterceptor();
	}

	@Test
	public void testAddHttpRequestInterceptor() {
		handler.addHttpRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws org.apache.http.HttpException, IOException {				
			}
		});
	}

	@Test
	public void testAddHttpResponseInterceptor() {
		handler.addHttpResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context)
					throws org.apache.http.HttpException, IOException {				
			}
		});
	}

}
