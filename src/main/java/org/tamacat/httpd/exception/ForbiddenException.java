/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

/**
 * Throws 403 Forbidden.
 */
public class ForbiddenException extends HttpException {

	private static final long serialVersionUID = 1L;

	public ForbiddenException(){
		super(HttpStatus.SC_FORBIDDEN);
	}
	
	public ForbiddenException(String message) {
		super(HttpStatus.SC_FORBIDDEN, message);
	}
	
	public ForbiddenException(Throwable cause) {
		super(HttpStatus.SC_FORBIDDEN, cause);
	}
}
