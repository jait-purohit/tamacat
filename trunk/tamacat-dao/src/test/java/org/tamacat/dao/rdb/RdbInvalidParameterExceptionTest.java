package org.tamacat.dao.rdb;

import static org.junit.Assert.*;

import org.junit.Test;

public class RdbInvalidParameterExceptionTest {

	@Test
	public void testRdbInvalidParameterException() {
		RdbInvalidParameterException e = new RdbInvalidParameterException();
		assertEquals(null, e.getMessage());
		assertEquals(null, e.getCause());
	}

	@Test
	public void testRdbInvalidParameterExceptionString() {
		RdbInvalidParameterException e = new RdbInvalidParameterException("TEST ERROR");
		assertEquals("TEST ERROR", e.getMessage());
		assertEquals(null, e.getCause());
	}

	@Test
	public void testRdbInvalidParameterExceptionThrowable() {
		Exception cause = new RuntimeException("TEST ERROR");
		RdbInvalidParameterException e = new RdbInvalidParameterException(cause);
		assertEquals("java.lang.RuntimeException: TEST ERROR", e.getMessage());
		assertSame(cause, e.getCause());
	}

	@Test
	public void testRdbInvalidParameterExceptionStringThrowable() {
		Exception cause = new RuntimeException("CAUSE ERROR");
		RdbInvalidParameterException e = new RdbInvalidParameterException("TEST ERROR", cause);
		assertEquals("TEST ERROR", e.getMessage());
		assertEquals("CAUSE ERROR", e.getCause().getMessage());
	}
}
