/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

public enum ReadyState {

	CONNECTING(0),
	OPEN(1),
	CLOSING(2),
	CLOSED(3);
	
	private final int state;
	
	ReadyState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
}
