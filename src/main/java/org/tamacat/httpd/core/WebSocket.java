package org.tamacat.httpd.core;

import java.net.Socket;

public class WebSocket {

	private Socket socket;
	private boolean isWebSocket;
	
	public WebSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void setWebSocket(boolean isWebSocket) {
		this.isWebSocket = isWebSocket;
	}
	
	public boolean isWebSocket() {
		return isWebSocket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
}
