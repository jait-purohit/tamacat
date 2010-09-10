/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class WebSocketImpl implements WebSocket {
	protected HttpRequest request;
	protected HttpResponse response;
	protected String protocol;
	protected ReadyState state;
	protected Throwable err;
	protected Outbound outbound;
	
	private static Set<WebSocketImpl> MEMBERS = new CopyOnWriteArraySet<WebSocketImpl>();

	  
	public WebSocketImpl(HttpRequest request, HttpResponse response, String protocol) {
		this.request = request;
		this.response = response;
		this.protocol = protocol;
	}
	
	@Override
	public ReadyState getReadyState() {
		return state;
	}

	@Override
	public void onOpen(Outbound outbound) {
		this.outbound = outbound;
		this.outbound.connect();
		state = ReadyState.OPEN;
		MEMBERS.add(this);
		System.out.println("onOpen()");
	}

	@Override
	public void onMessage(String data) {
		state = ReadyState.CONNECTING;
		System.out.println("onMessage() " + data);
		for (WebSocketImpl ws : MEMBERS) {
			try {
				ws.getOutbound().sendMessage(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Outbound getOutbound() {
		return outbound;
	}

	@Override
	public void onError(Throwable err) {
		this.err = err;
		state = ReadyState.CLOSING;
		System.out.println("onError()");
		throw new WebSocketException(err);
	}

	@Override
	public void onClose() {
		state = ReadyState.CLOSING;
		MEMBERS.remove(this);
		System.out.println("onClose()");
	}

	public HttpRequest getHttpRequest() {
		return request;
	}
	
	public String getProtocol() {
		return protocol;
	}
}
