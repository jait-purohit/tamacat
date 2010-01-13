package org.tamacat.httpd.page;

import static org.junit.Assert.*;
import org.junit.Test;

public class VelocityPageTest {

	@Test
	public void testGetTemplate() throws Exception {
		VelocityPage page = new VelocityPage("./src/test/resources/htdocs/web");
		assertNotNull(page.getTemplate("/index.vm"));
	}
}
