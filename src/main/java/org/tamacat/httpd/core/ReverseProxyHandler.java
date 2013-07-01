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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.protocol.HTTP;
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
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpHandler} for reverse proxy.
 */
public class ReverseProxyHandler extends AbstractHttpHandler {
	
	static final Log LOG = LogFactory.getLog(ReverseProxyHandler.class);
	
	protected static final String HTTP_OUT_CONN = "http.proxy.out-conn";
	protected static final String HTTP_CONN_KEEPALIVE = "http.proxy.conn-keepalive";
	protected static final String HTTP_KEEPALIVE_TIMEOUT = "http.proxy.keepalive-timeout";
    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
    protected static final String CHECK_INFINITE_LOOP
    	= ReverseProxyHandler.class.getName() + "_CHECK_INFINITE_LOOP";
    
	protected HttpRequestExecutor httpexecutor;
	protected HttpParamsBuilder builder = new HttpParamsBuilder();
	protected HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();
	protected PlainSocketFactory socketFactory = PlainSocketFactory.getSocketFactory();
	protected String proxyAuthorizationHeader = "X-ReverseProxy-Authorization";
	protected ReverseProxyConnectionReuseStrategy connStrategy;
	protected int keepAliveTimeout = 30000; //msec.

	/**
	 * <p>Default constructor.
	 */
	public ReverseProxyHandler() {
		this.httpexecutor = new HttpRequestExecutor();
		setDefaultHttpRequestInterceptor();
		this.connStrategy = new ReverseProxyConnectionReuseStrategy();
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
    	builder.socketTimeout(serviceUrl.getServerConfig().getParam("BackEndSocketTimeout", 5000))
    	  .connectionTimeout(serviceUrl.getServerConfig().getParam("BackEndConnectionTimeout", 10000))
          .socketBufferSize(serviceUrl.getServerConfig().getParam("BackEndSocketBufferSize", (8*1024)));
    }
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, 
			HttpContext context) {
		//Bugfix: java.lang.IllegalStateException: Content has been consumed
		//RequestUtils.setParameters(request, context, encoding);

		try {
			for (RequestFilter filter : requestFilters) {
				filter.doFilter(request, response, context);
			}
			doRequest(request, response, context);
		} catch (Exception e) {
			LOG.trace(e.getMessage());
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

        // Get the target server Connection Keep-Alive header. //
        boolean keepAlive = false;
        Header conh = request.getFirstHeader("Connection");
        if (conh != null && HTTP.CONN_CLOSE.equalsIgnoreCase(conh.getValue())) {
        	context.setAttribute(HTTP_CONN_KEEPALIVE, new Boolean(false));
        } else if (request.getProtocolVersion().greaterEquals(HttpVersion.HTTP_1_1)) {
        	keepAlive = this.connStrategy.keepAlive(targetResponse, context);
        	if (keepAlive) {
        		LOG.trace("Set Keep-Alive Timeout: " + keepAliveTimeout + " msec.");
        		context.setAttribute(HTTP_KEEPALIVE_TIMEOUT, new Integer(keepAliveTimeout));
        	}
        	context.setAttribute(HTTP_CONN_KEEPALIVE, new Boolean(keepAlive));
        }
        LOG.debug("Keep-Alive: " + keepAlive);

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
		HttpProcessor httpproc = procBuilder.build();
        LOG.trace(">> Request URI: " + request.getRequestLine());

//		Object loop = context.getAttribute(CHECK_INFINITE_LOOP);
//		if (loop == null) {
//			context.setAttribute(CHECK_INFINITE_LOOP, Boolean.TRUE);
//		} else {
//        	throw new ServiceUnavailableException("reverseUrl is infinite loop.");
//		}
        Socket outsocket = null;
        ReverseUrl reverseUrl = serviceUrl.getReverseUrl();
		try {
	        if (reverseUrl == null) {
	        	throw new ServiceUnavailableException("reverseUrl is null.");
	        }
	        context.setAttribute("reverseUrl", reverseUrl);
	        outsocket = socketFactory.createSocket();
	        
	        InetAddress remoteAddress = InetAddress.getByName(reverseUrl.getTargetAddress().getHostName());
	        InetSocketAddress remote = new InetSocketAddress(remoteAddress, reverseUrl.getTargetAddress().getPort());
	        outsocket = socketFactory.connectSocket(outsocket, remote, null, builder.buildParams());

	        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
	        context.setAttribute(HTTP_OUT_CONN, conn); //WokerThread close the client connection.
	        conn.bind(outsocket, builder.buildParams());
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
	        
	        //forward remote user.
	        ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
	        try {
		        reverseUrl.countUp();
		        httpexecutor.preProcess(targetRequest, httpproc, context);
		        HttpResponse targetResponse = httpexecutor.execute(targetRequest, conn, context);
		        httpexecutor.postProcess(targetResponse, httpproc, context);
		        return targetResponse;
	        } finally {
		        reverseUrl.countDown();
		        if (LOG.isDebugEnabled()) {
		        	LOG.debug(">> "+ reverseUrl.getReverse() + ", connections=" + reverseUrl.getActiveConnections());
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
	
	/**
	 * force disabled Keep-Alive.
	 * @param disabledKeepAlive
	 * @since 1.0.5
	 */
	public void setDisabledKeepAlive(boolean disabledKeepAlive) {
		this.connStrategy.setDisabledKeepAlive(disabledKeepAlive);
	}
	
	/**
	 * Always Keep-Alive. (Priority is given over disabledKeepAlive.)
	 * @param alwaysKeepAlive
	 * @since 1.0.6
	 */
	public void setAlwaysKeepAlive(boolean alwaysKeepAlive) {
		this.connStrategy.setAlwaysKeepAlive(alwaysKeepAlive);
	}
	
	/**
	 * Set the Keep-Alive Timeout.
	 * @param keepAliveTimeout (msec)
	 * @since 1.0.6
	 */
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}
}
