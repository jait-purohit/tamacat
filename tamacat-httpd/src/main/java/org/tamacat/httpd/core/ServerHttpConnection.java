package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.impl.DefaultBHttpServerConnection;

public class ServerHttpConnection extends DefaultBHttpServerConnection {

	public ServerHttpConnection(int buffersize) {
		super(buffersize);
	}

	private SocketWrapper socketWrapper;

	@Override
	public void bind(final Socket socket) throws IOException {
		socketWrapper = new SocketWrapper(socket);
		super.bind(socket);
	}

//	@Override
//    protected HttpRequestFactory createHttpRequestFactory() {
//        if (socketWrapper.isWebSocketSupport()) {
//        	return new WebSocketRequestFactory();
//        //} else if (socketWrapper.isWebDAVSupport()){
//        //	return new WebDavHttpRequestFactory();
//        } else {
//        	return new DefaultHttpRequestFactory();
//        }
//    }

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
