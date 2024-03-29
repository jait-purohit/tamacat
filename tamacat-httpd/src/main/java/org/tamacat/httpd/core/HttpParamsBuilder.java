/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.params.BasicHttpParams;


import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 * <p>The builder class for {@link HttpParams}.<br>
 * The {@link #buildParams} method execute a build {@code HttpParams} and returns.
 *
 *  @deprecated (1.1) use configuration classes provided 'org.apache.http.config'
 *  and 'org.apache.http.client.config'
 */
@Deprecated
public class HttpParamsBuilder {

	//default
	private int serverSocketTimeout = 5000; //5ms
	private int connectionTimeout = 30000; //30ms
	private int serverSocketBufferSize = 8*1024; //8192
	private boolean staleConnectionCheck = false;
	private boolean tcpNoDelay = true;
	private String originServer = "tamacat-httpd";

	/**
	 * <p>Set a server socket timeout, default {@code 5000} ms.
	 * @param serverSocketTimeout
	 * @return milli seconds of server socket timeout.
	 */
	public HttpParamsBuilder socketTimeout(int serverSocketTimeout) {
		this.serverSocketTimeout = serverSocketTimeout;
		return this;
	}

	/**
	 * <p>Set a connection timeout, default {@code 30000} ms.
	 * @param connectionTimeout
	 * @return milli seconds of connection timeout.
	 */
	public HttpParamsBuilder connectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	/**
	 * <p>Set a server socket buffer siize, default {@code 8192} bytes.
	 * @param serverSocketBufferSize
	 * @return bytes of server socket buffers.
	 */
	public HttpParamsBuilder socketBufferSize(int serverSocketBufferSize) {
		this.serverSocketBufferSize = serverSocketBufferSize;
		return this;
	}

	/**
	 * <p>Set a boolean value of stale connection check.
	 * @param staleConnectionCheck
	 * @return default false.
	 */
	public HttpParamsBuilder staleConnectionCheck(boolean staleConnectionCheck) {
		this.staleConnectionCheck = staleConnectionCheck;
		return this;
	}

	/**
	 * <p>Set a boolean value of tcpNoDelay.
	 * @param tcpNoDelay
	 * @return default true.
	 */
	public HttpParamsBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}

	/**
	 * <p>Set a origin server name.
	 * @param originServer
	 * @return default "tamacat-httpd"
	 */
	public HttpParamsBuilder originServer(String originServer) {
		this.originServer = originServer;
		return this;
	}

	/**
	 * Create a new {@code HttpParams} and returns.
	 * @return Implements of {@code HttpParams}.
	 */
	public HttpParams buildParams() {
		return new BasicHttpParams()
			.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, serverSocketTimeout)
			.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout)
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, serverSocketBufferSize)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, staleConnectionCheck)
			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, tcpNoDelay)
			.setParameter(CoreProtocolPNames.ORIGIN_SERVER, originServer);
	}
}
