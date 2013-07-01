package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.tamacat.httpd.exception.NotFoundException;

public class LocalFileHttpHandlerTest {

	@Test
	public void testGetDecodeUri() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertEquals("/", handler.getDecodeUri("/"));
		assertEquals("/test/index.html", handler.getDecodeUri("/test/index.html"));
		assertEquals("///", handler.getDecodeUri("///"));

		assertEquals("/\\index.html", handler.getDecodeUri("/\\index.html"));
		
		assertEquals("/ index.html", handler.getDecodeUri("/%20index.html"));
		assertEquals("/..", handler.getDecodeUri("/%2e%2e"));
		assertEquals("/.", handler.getDecodeUri("/%2e"));
		assertEquals("/./index.html", handler.getDecodeUri("/%2e/index.html"));
		assertEquals("///index.html", handler.getDecodeUri("/%2f/index.html"));
		
		try {
			handler.getDecodeUri("/%2e%2e/index.html");
			fail();
		} catch (NotFoundException e) {
		}
		try {
			handler.getDecodeUri("/%2e%2e%2findex.html");
			fail();
		} catch (NotFoundException e) {
		}
		try {
			handler.getDecodeUri("/../");
			fail();
		} catch (NotFoundException e) {
		}
		try {
			handler.getDecodeUri("../");
			fail();
		} catch (NotFoundException e) {
		}
		
		try {
			handler.getDecodeUri("..\\index.html");
			fail();
		} catch (NotFoundException e) {
		}
	}
}
