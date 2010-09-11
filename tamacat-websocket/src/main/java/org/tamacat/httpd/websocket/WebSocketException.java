/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

public class WebSocketException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public WebSocketException(Throwable cause) {
		super(cause);
	}

	public WebSocketException(String message, Throwable cause) {
		super(message, cause);
	}
}
