/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebSocketImpl implements WebSocket {
	static final Log LOG = LogFactory.getLog(WebSocketImpl.class);
	
	protected ReadyState state = ReadyState.CLOSED;
	protected Throwable err;
	protected Outbound outbound;
	
	private static Set<WebSocketImpl> MEMBERS = new CopyOnWriteArraySet<WebSocketImpl>();
	
	@Override
	public ReadyState getReadyState() {
		return state;
	}

	@Override
	public void onOpen(Outbound outbound) {
		if (state != ReadyState.CLOSED) {
			throw new IllegalStateException("WebSocket already opend.");
		}
		this.outbound = outbound;
		this.outbound.connect();
		state = ReadyState.OPEN;
		MEMBERS.add(this);
		LOG.debug("onOpen()");
	}

	@Override
	public void onMessage(String data) {
		if (state == ReadyState.CLOSED) {
			throw new IllegalStateException("WebSocket already closed.");
		}
		state = ReadyState.CONNECTING;
		LOG.debug("onMessage() " + data);
		for (WebSocketImpl ws : MEMBERS) {
			try {
				ws.getOutbound().sendMessage(data);
			} catch (IOException e) {
				LOG.warn(e.getMessage());
				LOG.trace(e);
			}
		}
		state = ReadyState.OPEN;
	}
	
	@Override
	public Outbound getOutbound() {
		return outbound;
	}

	@Override
	public void onError(Throwable err) {
		this.err = err;
		LOG.debug("onError()");
		throw new WebSocketException(err);
	}

	@Override
	public void onClose() {
		state = ReadyState.CLOSING;
		try {
			getOutbound().disconnect();
		} finally {
			MEMBERS.remove(this);
		}
		LOG.debug("onClose()");
		state = ReadyState.CLOSED;
	}
}
