package org.tamacat.httpd.core;

import java.net.Socket;

import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.jmx.PerformanceCounter;

public interface Worker extends Runnable {

	void setServerConfig(ServerConfig serverConfig);

	void setHttpService(HttpService httpService);

	void setSocket(Socket socket);

	void setPerformanceCounter(PerformanceCounter counter);
}
