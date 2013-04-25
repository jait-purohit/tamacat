package org.tamacat.httpd.util;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class ResponseUtilsTest {

	HttpResponse response;
	
	@Before
	public void setUp() throws Exception {
		response = HttpObjectFactory.createHttpResponse(200, "OK");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testResponseUtils() {
		new ResponseUtils();
	}
	
	@Test
	public void testSetEntity() throws UnsupportedEncodingException {
		ResponseUtils.setEntity(response, new StringEntity("<html></html>"));
	}
}
