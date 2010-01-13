/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

/**
 * <p>Throws 401 Unauthorized
 */
public class UnauthorizedException extends HttpException {

	private static final long serialVersionUID = 1L;
	
	public UnauthorizedException() {
		super(HttpStatus.SC_UNAUTHORIZED);
	}
	
	public UnauthorizedException(String message) {
		super(HttpStatus.SC_UNAUTHORIZED, message);
	}
}
