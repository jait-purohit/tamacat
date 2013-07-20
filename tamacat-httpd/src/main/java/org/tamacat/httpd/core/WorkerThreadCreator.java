package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.jmx.PerformanceCounter;

public class WorkerThreadCreator {

	HttpService httpService;
	ServerSocket serverSocket;
	ServerConfig serverConfig;
	PerformanceCounter counter;

	public WorkerThreadCreator() {}

	public WorkerThreadCreator(ServerConfig serverConfig,
			HttpService httpService, ServerSocket serverSocket) {
		this.serverConfig = serverConfig;
		this.httpService = httpService;
		this.serverSocket = serverSocket;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public void setCounter(PerformanceCounter counter) {
		this.counter = counter;
	}

	Worker accept(Socket socket) {
		Worker worker = new DefaultWorker();
		worker.setServerConfig(serverConfig);
		worker.setHttpService(httpService);
		worker.setSocket(socket);
		return worker;
	}

	public Runnable createWorkerThread() throws IOException {
		return accept(serverSocket.accept());
	}

	interface Worker extends Runnable {
		void setServerConfig(ServerConfig serverConfig);

		void setHttpService(HttpService httpService);

		void setSocket(Socket socket);

		void setPerformanceCounter(PerformanceCounter counter);
	}
}
