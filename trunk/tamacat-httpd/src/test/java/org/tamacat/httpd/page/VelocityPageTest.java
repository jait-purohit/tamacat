package org.tamacat.httpd.page;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;
import org.tamacat.util.PropertyUtils;

public class VelocityPageTest {

	@Test
	public void testGetTemplate() throws Exception {
		Properties props = PropertyUtils.getProperties("velocity.properties");
		VelocityPage page = new VelocityPage(props);
		page.init("./src/test/resources/htdocs/web");
		assertNotNull(page.getTemplate("/index.vm"));
	}
}
