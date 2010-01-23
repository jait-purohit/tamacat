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
		//display-name
		assertEquals("test", webapp.getDisplayName());
		//description
		assertEquals("Test servlets", webapp.getDescription());
		
		//context-param
		Map<String,String> contextParams = webapp.getContextParams();
		assertEquals("value1", contextParams.get("context-param1"));

		//servlet
		List<ServletDefine> servlets = webapp.getServlets();
		assertEquals(2, servlets.size());
		//servlet-1
		ServletDefine servletDefine1 = servlets.get(0);
		assertEquals("test1", servletDefine1.getServletName());
		assertEquals("org.tamacat.servlet.test.TestServlet", servletDefine1.getServletClass());
		Map<String,String> initParams1 = servletDefine1.getInitParams();
		assertEquals("value1", initParams1.get("init-param1"));
		
		//servlet-2
		ServletDefine servletDefine2 = servlets.get(1);
		assertEquals("test2", servletDefine2.getServletName());
		assertEquals("org.tamacat.servlet.test.TestServlet", servletDefine2.getServletClass());
		Map<String,String> initParams2 = servletDefine2.getInitParams();
		assertEquals("value2", initParams2.get("init-param2"));
		
		//servlet-mapping
		List<ServletMapping> mappings = webapp.getServletMapping();
		assertEquals(3, mappings.size());
		//mapping-1
		assertEquals("test1", mappings.get(0).getServletName());
		assertEquals("/test1/*", mappings.get(0).getUrlPattern());
		
		//mapping-2
		assertEquals("test1", mappings.get(1).getServletName());
		assertEquals("*.html", mappings.get(1).getUrlPattern());
		
		//mapping-3
		assertEquals("test2", mappings.get(2).getServletName());
		assertEquals("*.do", mappings.get(2).getUrlPattern());
	}
}
