/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExceptionUtilsTest {

	@Test
	public void testGetStackTrace() {
		Exception e = new Exception("test");
		String trace = ExceptionUtils.getStackTrace(e);
		assertTrue(trace.startsWith("java.lang.Exception: test"));
	}

	@Test
	public void testIsRuntime() {
		assertFalse(ExceptionUtils.isRuntime(new Exception("test")));
		assertTrue(ExceptionUtils.isRuntime(new RuntimeException("test")));
	}

	@Test
	public void testGetCauseException() {
		assertNull(ExceptionUtils.getCauseException(null));

		Exception e = new Exception("test");
		assertEquals(e, ExceptionUtils.getCauseException(e));
		
		Exception cause = new Exception("cause");
		e = new Exception(cause);
		assertEquals(cause, ExceptionUtils.getCauseException(e));
	}
}
