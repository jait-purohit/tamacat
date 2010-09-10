package org.tamacat.httpd.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.http.HttpServerConnection;
import org.tamacat.httpd.core.ServerHttpConnection;

public class WebSocketConnection implements WebSocket.Outbound {

	private final HttpServerConnection connection;
	private WebSocket websocket;
	
	String key1;
	String key2;
	
	WebSocketConnection(HttpServerConnection connection, WebSocket websocket) {
		this.connection = connection;
		this.websocket = websocket;
	}
	
    public void setHixieKeys(String key1,String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }
    
	@Override
	public void sendMessage(String data) throws IOException {
		System.out.println("sendMessage: " + connection);
//		if (connection instanceof ServerHttpConnection) {
//			WebSocketWorkerThread worker = new WebSocketWorkerThread(
//				(ServerHttpConnection)connection, websocket);
//			worker.start();
            //disconnect();
			send(data);
//		}
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

	protected void send(String data) throws IOException {
		if (connection instanceof ServerHttpConnection) {
			Socket socket = ((ServerHttpConnection)connection).getSocket();
			OutputStream out = socket.getOutputStream();
			WebSocketUtils.getEntity(data).writeTo(out);
		}
	}
	
	@Override
	public void connect() {
		if (connection instanceof ServerHttpConnection) {
			WebSocketWorkerThread worker = new WebSocketWorkerThread(
				(ServerHttpConnection)connection, websocket);
			worker.start();
		}
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

	public void flush() {
		try {
			connection.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
