package org.tamacat.httpd.page;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.HttpStatus;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class VelocityErrorPageTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetErrorPageHttpRequestHttpResponseHttpException() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		VelocityErrorPage page = new VelocityErrorPage();
		
		try {
			HttpException exception = new HttpException(
				HttpStatus.SC_INTERNAL_SERVER_ERROR, "Test Error.");
			String html = page.getErrorPage(request, response, exception);
			assertNotNull(html);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetTemplate() {
		VelocityErrorPage page = new VelocityErrorPage();
		try {
			StringWriter writer = new StringWriter();
			Template template = page.getTemplate("error500.vm");
			
			VelocityContext context = new VelocityContext();
   			template.merge(context, writer);
			assertNotNull(writer.toString());
		} catch (Exception e) {
			fail();
		}
	}

}
