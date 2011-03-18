/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

public class RdbInvalidParameterException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public RdbInvalidParameterException() {
	}

	public RdbInvalidParameterException(String s) {
		super(s);
	}

	public RdbInvalidParameterException(Throwable cause) {
		super(cause);
	}

	public RdbInvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}
}
