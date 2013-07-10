/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.PlainSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.jmx.PerformanceCounter;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpHandler} for reverse proxy.
 */
public class ReverseProxyHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(ReverseProxyHandler.class);

	protected static final String HTTP_OUT_CONN = "http.proxy.out-conn";
	protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	protected HttpRequestExecutor httpexecutor;
	protected HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();
	protected PlainSocketFactory socketFactory = PlainSocketFactory.getSocketFactory();
	protected String proxyAuthorizationHeader = "X-ReverseProxy-Authorization";
	protected String proxyOrignPathHeader = "X-ReverseProxy-Origin-Path"; //v1.1
	protected int connectionTimeout = 30000;
	protected int socketBufferSize = 8192;
	protected SocketConfig config;

	/**
	 * <p>Default constructor.
	 */
	public ReverseProxyHandler() {
		this.httpexecutor = new HttpRequestExecutor();
		setDefaultHttpRequestInterceptor();
	}

	/**
	 * <p>Get the backend server configuration parameters
	 * from the server.properties.
	 *
	 * <p> default value is:
	 * <pre>
	 * BackEndSocketTimeout=5000
	 * BackEndConnectionTimeout=10000
	 * BackEndSocketBufferSize=8192
	 * </pre>
	 */
	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		super.setServiceUrl(serviceUrl);
		config = SocketConfig.custom().setSoTimeout(
			serviceUrl.getServerConfig().getParam("BackEndSocketTimeout", 5000)).build();
		connectionTimeout = serviceUrl.getServerConfig().getParam("BackEndConnectionTimeout", 30000);
		socketBufferSize = serviceUrl.getServerConfig().getParam("BackEndSocketBufferSize", (8*1024));
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) {
		try {
			for (RequestFilter filter : requestFilters) {
				filter.doFilter(request, response, context);
			}
			doRequest(request, response, context);
		} catch (Exception e) {
			handleException(request, response, e);
		} finally {
			for (ResponseFilter filter : responseFilters) {
				filter.afterResponse(request, response, context);
			}
		}
	}

	@Override
	public void doRequest(
			HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		// Set the X-Forwarded Headers.
		ReverseUtils.setXForwardedFor(request, context);
		ReverseUtils.setXForwardedHost(request);

		// Access Backend server. //
		HttpResponse targetResponse = forwardRequest(request, response, context);

		ReverseUrl reverseUrl = serviceUrl.getReverseUrl();
		ReverseUtils.copyHttpResponse(targetResponse, response);
		ReverseUtils.rewriteStatusLine(request, response);
		ReverseUtils.rewriteContentLocationHeader(request, response, reverseUrl);

		ReverseUtils.rewriteServerHeader(response, reverseUrl);

		// Location Header convert. //
		ReverseUtils.rewriteLocationHeader(request, response, reverseUrl);

		// Set-Cookie Header convert. //
		ReverseUtils.rewriteSetCookieHeader(request, response, reverseUrl);

		// Set the entity and response headers from targetResponse. //
		response.setEntity(targetResponse.getEntity());
	}

	/**
	 * <p>Request forwarding to backend server.
	 * @param request
	 * @param response
	 * @param context
	 * @return {@code HttpResponse}
	 */
	protected HttpResponse forwardRequest(
			HttpRequest request, HttpResponse response, HttpContext context) {
		if (LOG.isDebugEnabled()){
			LOG.info(">> " + request.getRequestLine().getMethod() + " " + request.getRequestLine().getUri() + " " + request.getProtocolVersion());
		}

		Socket outsocket = null;
		ReverseUrl reverseUrl = serviceUrl.getReverseUrl();
		try {
			if (reverseUrl == null) {
				throw new ServiceUnavailableException("reverseUrl is null.");
			}
			context.setAttribute("reverseUrl", reverseUrl);
			outsocket = socketFactory.createSocket(context);

			InetAddress remoteAddress = InetAddress.getByName(reverseUrl.getTargetAddress().getHostName());
			InetSocketAddress remote = new InetSocketAddress(remoteAddress, reverseUrl.getTargetAddress().getPort());
			HttpHost target = reverseUrl.getTargetHost();
			socketFactory.connectSocket(connectionTimeout, outsocket, target, remote, null, context);

			DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(socketBufferSize);
			context.setAttribute(HTTP_OUT_CONN, conn); //WokerThread close the client connection.
			conn.bind(outsocket);

			if (LOG.isTraceEnabled()) {
				LOG.trace("Outgoing connection to " + outsocket.getInetAddress());
				LOG.trace("request: " + request);
			}

			ReverseHttpRequest targetRequest = null;
			if (request instanceof HttpEntityEnclosingRequest) {
				targetRequest = new ReverseHttpEntityEnclosingRequest(request, context, reverseUrl);
			} else {
				targetRequest = new ReverseHttpRequest(request, context, reverseUrl);
			}

			targetRequest.setHeader(proxyOrignPathHeader, serviceUrl.getPath()); //v1.1

			//forward remote user.
			ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
			try {
				HttpProcessor httpproc = procBuilder.build();
				if (reverseUrl instanceof PerformanceCounter) {
					((PerformanceCounter)reverseUrl).countUp();
				}
				httpexecutor.preProcess(targetRequest, httpproc, context);
				HttpResponse targetResponse = httpexecutor.execute(targetRequest, conn, context);
				httpexecutor.postProcess(targetResponse, httpproc, context);
				return targetResponse;
			} finally {
				if (reverseUrl instanceof PerformanceCounter) {
					((PerformanceCounter)reverseUrl).countDown();
				}
			}
		} catch (SocketException e) {
			throw new ServiceUnavailableException(
				BasicHttpStatus.SC_GATEWAY_TIMEOUT.getReasonPhrase() + " URL=" + reverseUrl.getReverse());
		} catch (RuntimeException e) {
			handleException(request, response, e);
			return response;
		} catch (ConnectTimeoutException e) {
			throw new HttpException(BasicHttpStatus.SC_GATEWAY_TIMEOUT, e);
		} catch (Exception e) {
			handleException(request, response, e);
			return response;
		}
	}

	/**
	 * <p>Preset the HttpRequestInterceptor.
	 */
	protected void setDefaultHttpRequestInterceptor() {
		procBuilder.addInterceptor(new RequestContent())
		.addInterceptor(new RequestTargetHost())
		.addInterceptor(new RequestConnControl())
		.addInterceptor(new RequestUserAgent())
		.addInterceptor(new RequestExpectContinue());
	}

	public void addHttpRequestInterceptor(HttpRequestInterceptor interceptor) {
		procBuilder.addInterceptor(interceptor);
	}

	public void addHttpResponseInterceptor(HttpResponseInterceptor interceptor) {
		procBuilder.addInterceptor(interceptor);
	}

	/**
	 * Set the header name of Reverse Proxy Authorization.
	 * default: "X-ReverseProxy-Authorization"
	 * @param proxyAuthorizationHeader
	 */
	public void setProxyAuthorizationHeader(String proxyAuthorizationHeader) {
		this.proxyAuthorizationHeader = proxyAuthorizationHeader;
	}

	/**
	 * Set the header name of Reverse Proxy Origin Path.
	 * default: "X-ReverseProxy-Origin-Path"
	 * @param proxyOrignPathHeader
	 * @since 1.1
	 */
	public void setProxyOrignPathHeader(String proxyOrignPathHeader) {
		this.proxyOrignPathHeader = proxyOrignPathHeader;
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html, encoding);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}

	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, ContentType.create(getContentType(file)));
		return body;
	}
}
