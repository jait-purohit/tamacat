package org.tamacat.httpd.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.apache.http.HttpServerConnection;
import org.tamacat.httpd.core.ServerHttpConnection;
import org.tamacat.httpd.core.ThreadExecutorFactory;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebSocketConnection implements WebSocket.Outbound {

	static final Log LOG = LogFactory.getLog(WebSocketConnection.class);
	
	private final HttpServerConnection connection;
	private final WebSocket websocket;
    private ExecutorService executors;
    private String threadName = "WebSocket";
    private int maxThreads = 100;
    
	String key1;
	String key2;
	String key3;
	
	WebSocketConnection(HttpServerConnection connection, WebSocket websocket) {
		this.connection = connection;
		this.websocket = websocket;
	}
	
    public void setHixieKeys(String key1,String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }
    
    public byte[] getKey3() {
		if (key3 == null && connection instanceof ServerHttpConnection) {
			Socket socket = ((ServerHttpConnection)connection).getSocket();
			try {
				InputStream in = socket.getInputStream();
				int index = 0;
	            int ch;
	            byte[] buf = new byte[8];
	            while ((ch = in.read()) != -1) {
	                buf[index++] = (byte)ch;
	                if (index >= 8) break;
				}
				return buf;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
    }
    
	@Override
	public void sendMessage(String data) throws IOException {
		LOG.debug("sendMessage: " + connection);
		send(data);
	}

	@Override
	public void sendMessage(byte frame, String data) throws IOException {
		LOG.debug("sendMessage");
	}

	@Override
	public void sendMessage(byte frame, byte[] data) throws IOException {
		LOG.debug("sendMessage");
	}

	@Override
	public void sendMessage(byte frame, byte[] data, int offset, int length)
			throws IOException {
		LOG.debug("sendMessage");
	}

	public void send(String data) throws IOException {
		LOG.trace("send [" + data + "]");
		if (connection instanceof ServerHttpConnection) {
			Socket socket = ((ServerHttpConnection)connection).getSocket();
			OutputStream out = socket.getOutputStream();
			WebSocketUtils.getEntity(data).writeTo(out);
		}
	}
	
	@Override
	public void connect() {
		if (connection instanceof ServerHttpConnection) {
			executors = new ThreadExecutorFactory(threadName).getExecutorService(maxThreads);
			executors.execute(new WebSocketWorkerThread(
				(ServerHttpConnection)connection, websocket)
			);
		}
	}
	
	@Override
	public void disconnect() {
		try {
			connection.shutdown();
			LOG.debug("disconnect");
		} catch (IOException e) {
			LOG.warn(e.getMessage());
			LOG.trace(e);
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
			LOG.warn(e.getMessage());
			LOG.trace(e);
		}
	}
}
