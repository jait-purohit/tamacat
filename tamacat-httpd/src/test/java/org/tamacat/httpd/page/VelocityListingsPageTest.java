package org.tamacat.httpd.page;

import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.util.ClassUtils;

public class VelocityListingsPageTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetListingsPageHttpRequestHttpResponseFile() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		VelocityListingsPage page = new VelocityListingsPage();
		
		try {
			File file = new File(ClassUtils.getURL(".").toURI());
			String html = page.getListingsPage(request, response, file);
			assertNotNull(html);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetTemplate() {
		VelocityListingsPage page = new VelocityListingsPage();
		try {
			StringWriter writer = new StringWriter();
			Template template = page.getTemplate("listings.vm");
			
			VelocityContext context = new VelocityContext();
   			template.merge(context, writer);
			assertNotNull(writer.toString());
		} catch (Exception e) {
			fail();
		}
	}

}
