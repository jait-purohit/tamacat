/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.io.DefaultHttpRequestParserFactory;

public class ServerHttpConnection extends DefaultBHttpServerConnection {

	final long CONN_START = new Long(System.currentTimeMillis());

	public ServerHttpConnection(int buffersize, HttpRequestFactory factory) {
		super(buffersize, buffersize, null, null, null, null, null, new DefaultHttpRequestParserFactory(null, factory), null);
	}

	public ServerHttpConnection(int buffersize) {
		super(buffersize);
	}

	private SocketWrapper socketWrapper;

	public long getConnectionStartTime() {
		return CONN_START;
	}

	@Override
	public void bind(final Socket socket) throws IOException {
		socketWrapper = new SocketWrapper(socket);
		super.bind(socket);
	}

	@Override
	public Socket getSocket() {
		return super.getSocket();
	}

	public void setWebSocketSupport(boolean isWebSocket) {
		socketWrapper.setWebSocketSupport(isWebSocket);
	}

	public void setWebDAVSupport(boolean isWebSocketSupport) {
		socketWrapper.setWebDAVSupport(isWebSocketSupport);
	}

	public SocketWrapper getSocketWrapper() {
		return socketWrapper;
	}
}
