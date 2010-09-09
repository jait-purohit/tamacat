package org.tamacat.httpd.websocket;

import java.io.IOException;

import org.apache.http.HttpConnection;

public class WebSocketConnection implements WebSocket.Outbound {

	private final HttpConnection connection;
	String key1;
	String key2;
	
	WebSocketConnection(HttpConnection connection) {
		this.connection = connection;
	}
	
    public void setHixieKeys(String key1,String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }
    
	@Override
	public void sendMessage(String data) throws IOException {
		System.out.println("sendMessage");
	}

	@Override
	public void sendMessage(byte frame, String data) throws IOException {
		System.out.println("sendMessage");
	}

	@Override
	public void sendMessage(byte frame, byte[] data) throws IOException {
		System.out.println("sendMessage");
	}

	@Override
	public void sendMessage(byte frame, byte[] data, int offset, int length)
			throws IOException {
		System.out.println("sendMessage");
	}

	@Override
	public void disconnect() {
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isOpen() {
		return connection.isOpen();
	}

}
