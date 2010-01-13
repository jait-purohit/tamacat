/*
 * Copyright (c) 2009, TamaCat.org
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
 */
public class HttpParamsBuilder {

	private int serverSocketTimeout = 30000;
	private int serverSocketBufferSize = 8*1024;
	private boolean staleConnectionCheck = false;
	private boolean tcpNoDelay = true;
	private String originServer = "tamacat-httpd-0.4";
	
	/**
	 * <p>Set a server socket timeout, default {@code 30000} ms.
	 * @param serverSocketTimeout
	 * @return milli seconds of server socket timeout.
	 */
	public HttpParamsBuilder socketTimeout(int serverSocketTimeout) {
		this.serverSocketTimeout = serverSocketTimeout;
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
	 * @return default "tamacat-httpd-$version"
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
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, serverSocketBufferSize)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, staleConnectionCheck)
			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, tcpNoDelay)
			.setParameter(CoreProtocolPNames.ORIGIN_SERVER, originServer);
	}
}
