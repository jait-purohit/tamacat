/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

/**
 * Throws 404 Not Found.
 */
public class NotFoundException extends HttpException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super(HttpStatus.SC_NOT_FOUND);
	}

	public NotFoundException(String message) {
		super(HttpStatus.SC_NOT_FOUND, message);
	}

	public NotFoundException(Throwable cause) {
		super(HttpStatus.SC_NOT_FOUND, cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(HttpStatus.SC_NOT_FOUND, message, cause);
	}
}
