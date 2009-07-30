/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.params.BasicHttpParams;


import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

public class HttpParamsBuilder {

	private int serverSocketTimeout = 30000;
	private int serverSocketBufferSize = 8*1024;
	private boolean staleConnectionCheck = false;
	private boolean tcpNoDelay = true;
	private String originServer = "HttpComponents/1.1";
	
	public HttpParamsBuilder socketTimeout(int serverSocketTimeout) {
		this.serverSocketTimeout = serverSocketTimeout;
		return this;
	}
	
	public HttpParamsBuilder socketBufferSize(int serverSocketBufferSize) {
		this.serverSocketBufferSize = serverSocketBufferSize;
		return this;
	}
	
	public HttpParamsBuilder staleConnectionCheck(boolean staleConnectionCheck) {
		this.staleConnectionCheck = staleConnectionCheck;
		return this;
	}
	
	public HttpParamsBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}
	
	public HttpParamsBuilder originServer(String originServer) {
		this.originServer = originServer;
		return this;
	}
	
	public HttpParams build() {
		return new BasicHttpParams()
			.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, serverSocketTimeout)
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, serverSocketBufferSize)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, staleConnectionCheck)
			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, tcpNoDelay)
			.setParameter(CoreProtocolPNames.ORIGIN_SERVER, originServer);
	}
	
	public HttpParams buildParams() {
		return new BasicHttpParams()
			.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, serverSocketTimeout)
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, serverSocketBufferSize)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, staleConnectionCheck)
			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, tcpNoDelay)
			.setParameter(CoreProtocolPNames.ORIGIN_SERVER, originServer);
	}
}
