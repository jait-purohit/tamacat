/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

public class WebSocketException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public WebSocketException(Throwable arg0) {
		super(arg0);
	}

	public WebSocketException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
