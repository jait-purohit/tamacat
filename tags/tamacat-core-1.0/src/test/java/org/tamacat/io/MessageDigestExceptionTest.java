package org.tamacat.io;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessageDigestExceptionTest {

	@Test
	public void testMessageDigestException() {
		MessageDigestException e = new MessageDigestException();
		assertEquals(null, e.getMessage());
		assertEquals(null, e.getCause());
	}

	@Test
	public void testMessageDigestExceptionString() {
		MessageDigestException e = new MessageDigestException("Test Message");
		assertEquals("Test Message", e.getMessage());
		assertEquals(null, e.getCause());
	}

	@Test
	public void testMessageDigestExceptionThrowable() {
		Exception cause = new Exception("Test Message");
		MessageDigestException e = new MessageDigestException(cause);
		assertEquals("java.lang.Exception: Test Message", e.getMessage());
        assertEquals("Test Message", e.getCause().getMessage());
    }

	@Test
	public void testMessageDigestExceptionStringThrowable() {
		Exception cause = new Exception("Test Message1");
		MessageDigestException e = new MessageDigestException("Test Message2", cause);
        assertEquals("Test Message2", e.getMessage());
        assertEquals("Test Message1", e.getCause().getMessage());
    }

}
