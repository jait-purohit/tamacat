package org.tamacat.httpd.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang.ArrayUtils;
import org.tamacat.httpd.core.ServerHttpConnection;

public class WebSocketWorkerThread extends Thread implements Thread.UncaughtExceptionHandler {

	ServerHttpConnection conn;
	WebSocket websocket;
	
	WebSocketWorkerThread(ServerHttpConnection conn, WebSocket websocket) {
		this.conn = conn;
		this.websocket = websocket;
		conn.setSocketTimeout(10*60*1000);
	}
	
	public void run() {
		Socket socket = conn.getSocket();
		InputStream in;
		try {
			in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
        	int b = 0;
            while ((b = in.read()) == 0x00) {
                byte[] buf = new byte[256];
                int index = 0;
                while ((b = in.read()) != 0xFF) {
                    buf[index++] = (byte) b;
                }
                //debug
                System.out.println(this.getName() + ": " + new String(buf, 0, index,"UTF-8"));
       			WebSocketUtils.getEntity(
       				WebSocketUtils.getFrameData(
       					new String(ArrayUtils.subarray(buf, 0, index),"UTF-8")
       			    )
       			).writeTo(out);
            }
        } catch (IOException e) {
            websocket.onError(e);
        }
	}
	
	@Override
    public void uncaughtException(final Thread t, final Throwable e) {
        websocket.onError(e);
    }
}
