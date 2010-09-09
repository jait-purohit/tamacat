package org.tamacat.httpd.core;

import java.net.Socket;

import org.apache.http.impl.DefaultHttpServerConnection;

public class ServerHttpConnection extends DefaultHttpServerConnection {

	@Override
	public Socket getSocket() {
		return super.getSocket();
	}
}
