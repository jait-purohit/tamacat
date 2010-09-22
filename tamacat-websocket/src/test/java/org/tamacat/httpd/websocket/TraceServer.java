package org.tamacat.httpd.websocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TraceServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new Worker().start();
	}

	static class Worker extends Thread {
		ServerSocket serverSocket;
		Worker() throws Exception {
			serverSocket = new ServerSocket(8080);
		}
		
		public void run() {
			try {
				do {
					System.out.println("accept");
					Socket socket = serverSocket.accept();
					System.out.println("accepted");

					BufferedReader in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String line;
					while ((line = in.readLine()) != null) {
						System.out.println(line);
					}
					socket.close();
				} while (serverSocket != null && !serverSocket.isClosed());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
