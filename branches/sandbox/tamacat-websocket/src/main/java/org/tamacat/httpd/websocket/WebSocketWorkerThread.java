package org.tamacat.httpd.websocket;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.ArrayUtils;
import org.tamacat.httpd.core.ServerHttpConnection;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebSocketWorkerThread extends Thread implements Thread.UncaughtExceptionHandler {

	static final Log LOG = LogFactory.getLog(WebSocketWorkerThread.class);
	
	protected final ServerHttpConnection conn;
	protected final WebSocket websocket;
	
	public WebSocketWorkerThread(ServerHttpConnection conn, WebSocket websocket) {
		this.conn = conn;
		this.websocket = websocket;
	}
	
	public void run() {
		InputStream in = null;
		try {
			conn.setSocketTimeout(10*60*1000);
			Socket socket = conn.getSocket();
			in = socket.getInputStream();
        	int b = 0;
            while ((b = in.read()) == 0x00) {
                byte[] buf = new byte[256];
                int index = 0;
                while ((b = in.read()) != 0xFF) {
                    buf[index++] = (byte) b;
                }
   				String data = WebSocketUtils.getFrameData(
       				new String(ArrayUtils.subarray(buf, 0, index),"UTF-8")
       			);
   				WebSocketValidator.validate(data);
                LOG.trace(this.getName() + ": " + new String(buf, 0, index,"UTF-8"));
                websocket.onMessage(data);
            }
        } catch (SocketTimeoutException e) {
        	LOG.debug(e.getMessage());
        	websocket.onClose();
        } catch (Exception e) {
        	LOG.debug(e.getMessage());
            websocket.onError(e);
        }
	}
	
	@Override
    public void uncaughtException(final Thread t, final Throwable e) {
        websocket.onError(e);
    }
}
