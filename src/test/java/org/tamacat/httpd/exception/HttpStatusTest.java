package org.tamacat.httpd.exception;

import static org.junit.Assert.*;
import org.junit.Test;

public class HttpStatusTest {

	@Test
	public void testGetHttpStatus() {
		assertEquals(HttpStatus.SC_OK, HttpStatus.getHttpStatus(200));
		assertEquals(HttpStatus.SC_NOT_FOUND, HttpStatus.getHttpStatus(404));
		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.getHttpStatus(500));
	}

	@Test
	public void testGetStatusCode() {
		assertEquals(200, HttpStatus.SC_OK.getStatusCode());
		assertEquals(404, HttpStatus.SC_NOT_FOUND.getStatusCode());
	}

	@Test
	public void testGetReasonPhrase() {
		assertEquals("OK", HttpStatus.SC_OK.getReasonPhrase());
	}

	@Test
	public void testIsInformational() {
		assertEquals(true, HttpStatus.SC_CONTINUE.isInformational());
		assertEquals(false, HttpStatus.SC_NOT_FOUND.isInformational());
	}

	@Test
	public void testIsSuccess() {
		assertEquals(true, HttpStatus.SC_OK.isSuccess());
		assertEquals(false, HttpStatus.SC_NOT_FOUND.isSuccess());
	}

	@Test
	public void testIsRedirection() {
		assertEquals(true, HttpStatus.SC_MOVED_PERMANENTLY.isRedirection());
		assertEquals(false, HttpStatus.SC_OK.isRedirection());
	}

	@Test
	public void testIsClientError() {
		assertEquals(true, HttpStatus.SC_NOT_FOUND.isClientError());
		assertEquals(false, HttpStatus.SC_INTERNAL_SERVER_ERROR.isClientError());
	}

	@Test
	public void testIsServerError() {
		assertEquals(true, HttpStatus.SC_INTERNAL_SERVER_ERROR.isServerError());
		assertEquals(true, HttpStatus.SC_SERVICE_UNAVAILABLE.isServerError());
		assertEquals(false, HttpStatus.SC_NOT_FOUND.isServerError());
	}
}
