package org.tamacat.httpd.core;

import java.util.concurrent.ExecutorService;

import org.tamacat.httpd.config.ServerConfig;

public interface ExecutorFactory {

	void setName(String name);

	void setServerConfig(ServerConfig serverConfig);

	ExecutorService getExecutorService();

	void shutdown();
}
