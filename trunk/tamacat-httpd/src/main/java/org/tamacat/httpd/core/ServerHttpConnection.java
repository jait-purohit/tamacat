package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;

public class ServerHttpConnection extends DefaultHttpServerConnection {

	private WebSocket websocket;

	@Override
    public void bind(final Socket socket, final HttpParams params) throws IOException {
		websocket = new WebSocket(socket);
		super.bind(socket, params);
    }
	
	@Override
    protected HttpRequestFactory createHttpRequestFactory() {
        if (websocket.isWebSocket()) {
        	return new WebSocketRequestFactory();
        } else {
        	return new DefaultHttpRequestFactory();
        }
    }
    
	@Override
	public Socket getSocket() {
		return super.getSocket();
	}
	
	public void setWebSocket(boolean isWebSocket) {
		websocket.setWebSocket(isWebSocket);
	}
	
	public WebSocket getWebSocket() {
		return websocket;
	}
}
