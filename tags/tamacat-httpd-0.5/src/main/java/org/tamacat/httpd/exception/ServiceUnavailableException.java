/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

/**
 * <p>Throws 503 Service Unavailable.
 */
public class ServiceUnavailableException extends HttpException {

	private static final long serialVersionUID = 1L;

	public ServiceUnavailableException() {
		super(HttpStatus.SC_SERVICE_UNAVAILABLE);
	}

	public ServiceUnavailableException(Throwable cause) {
		super(HttpStatus.SC_SERVICE_UNAVAILABLE, cause);
	}

	public ServiceUnavailableException(String message) {
		super(HttpStatus.SC_SERVICE_UNAVAILABLE, message);
	}

	public ServiceUnavailableException(String message,
			Throwable cause) {
		super(HttpStatus.SC_SERVICE_UNAVAILABLE, message, cause);
	}
}
