package org.tamacat.httpd.exception;

import static org.junit.Assert.*;
import org.junit.Test;
import org.tamacat.httpd.core.BasicHttpStatus;

public class HttpStatusTest {

	@Test
	public void testGetHttpStatus() {
		assertEquals(BasicHttpStatus.SC_OK, BasicHttpStatus.getHttpStatus(200));
		assertEquals(BasicHttpStatus.SC_NOT_FOUND, BasicHttpStatus.getHttpStatus(404));
		assertEquals(BasicHttpStatus.SC_INTERNAL_SERVER_ERROR, BasicHttpStatus.getHttpStatus(500));
	}

	@Test
	public void testGetStatusCode() {
		assertEquals(200, BasicHttpStatus.SC_OK.getStatusCode());
		assertEquals(404, BasicHttpStatus.SC_NOT_FOUND.getStatusCode());
	}

	@Test
	public void testGetReasonPhrase() {
		assertEquals("OK", BasicHttpStatus.SC_OK.getReasonPhrase());
	}

	@Test
	public void testIsInformational() {
		assertEquals(true, BasicHttpStatus.SC_CONTINUE.isInformational());
		assertEquals(false, BasicHttpStatus.SC_NOT_FOUND.isInformational());
	}

	@Test
	public void testIsSuccess() {
		assertEquals(true, BasicHttpStatus.SC_OK.isSuccess());
		assertEquals(false, BasicHttpStatus.SC_NOT_FOUND.isSuccess());
	}

	@Test
	public void testIsRedirection() {
		assertEquals(true, BasicHttpStatus.SC_MOVED_PERMANENTLY.isRedirection());
		assertEquals(false, BasicHttpStatus.SC_OK.isRedirection());
	}

	@Test
	public void testIsClientError() {
		assertEquals(true, BasicHttpStatus.SC_NOT_FOUND.isClientError());
		assertEquals(false, BasicHttpStatus.SC_INTERNAL_SERVER_ERROR.isClientError());
	}

	@Test
	public void testIsServerError() {
		assertEquals(true, BasicHttpStatus.SC_INTERNAL_SERVER_ERROR.isServerError());
		assertEquals(true, BasicHttpStatus.SC_SERVICE_UNAVAILABLE.isServerError());
		assertEquals(false, BasicHttpStatus.SC_NOT_FOUND.isServerError());
	}
}
