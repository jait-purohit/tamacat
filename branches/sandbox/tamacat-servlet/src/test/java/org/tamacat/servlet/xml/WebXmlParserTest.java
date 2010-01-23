package org.tamacat.servlet.xml;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebXmlParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParse() {
		WebApp webapp = new WebXmlParser().parse("test/WEB-INF/web.xml");
		assertEquals("test", webapp.getDisplayName());
		assertEquals("Test servlets", webapp.getDescription());
		
		Map<String,String> contextParams = webapp.getContextParams();
		assertEquals("value1", contextParams.get("context-param1"));

		List<ServletDefine> servlets = webapp.getServlets();
		assertEquals(1, servlets.size());
		ServletDefine servletDefine = servlets.get(0);
		assertEquals("test1", servletDefine.getServletName());
		assertEquals("org.tamacat.servlet.test.TestServlet", servletDefine.getServletClass());
		Map<String,String> initParams = servletDefine.getInitParams();
		assertEquals("value1", initParams.get("init-param1"));
	}
}
