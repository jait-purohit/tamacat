/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
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

    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
    protected static final String CHECK_INFINITE_LOOP
    	= ReverseProxyHandler.class.getName() + "_CHECK_INFINITE_LOOP";
	protected HttpRequestExecutor httpexecutor;
	protected HttpProcessor httpproc;
	protected HttpParamsBuilder builder = new HttpParamsBuilder();
	protected HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();
	protected PlainSocketFactory socketFactory = PlainSocketFactory.getSocketFactory();
	protected String proxyAuthorizationHeader = "X-ReverseProxy-Authorization";

	protected ReverseUrl reverseUrl;
	PoolingClientConnectionManager cm;

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
    	builder.socketTimeout(serviceUrl.getServerConfig().getParam("BackEndSocketTimeout", 5000))
    	  .connectionTimeout(serviceUrl.getServerConfig().getParam("BackEndConnectionTimeout", 10000))
          .socketBufferSize(serviceUrl.getServerConfig().getParam("BackEndSocketBufferSize", (8*1024)));
    	
    	reverseUrl = serviceUrl.getReverseUrl();
        if (reverseUrl == null) {
        	throw new ServiceUnavailableException("reverseUrl is null.");
        }
        
    	Scheme http = new Scheme(reverseUrl.getReverse().getProtocol(),
    			reverseUrl.getTargetAddress().getPort(), PlainSocketFactory.getSocketFactory());
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
    	schemeRegistry.register(http);
    	cm = new PoolingClientConnectionManager(schemeRegistry);
    	cm.setMaxPerRoute(new HttpRoute(new HttpHost(reverseUrl.getTargetAddress().getHostName())), 10);
    	cm.setMaxTotal(20);
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

        // Access Backend server //
        HttpResponse targetResponse = forwardRequest(request, response, context);

        	//(ReverseUrl) context.getAttribute("reverseUrl");
        ReverseUtils.copyHttpResponse(targetResponse, response);
        ReverseUtils.rewriteContentLocationHeader(request, response, reverseUrl);
        
        // Location Header convert. //
        ReverseUtils.rewriteLocationHeader(request, response, reverseUrl);
        
        // Set-Cookie Header convert. //
        ReverseUtils.rewriteSetCookieHeader(request, response, reverseUrl);
        
        // Set the entity and response headers from targetResponse.
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
		this.httpproc = procBuilder.build();
        LOG.trace(">> Request URI: " + request.getRequestLine().getUri());

		Object loop = context.getAttribute(CHECK_INFINITE_LOOP);
		if (loop == null) {
			context.setAttribute(CHECK_INFINITE_LOOP, Boolean.TRUE);
		} else {
        	throw new ServiceUnavailableException("reverseUrl is infinite loop.");
		}
        //Socket outsocket = null;
		//DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        		
		try {
	        context.setAttribute("reverseUrl", reverseUrl);
	        ReverseUtils.setXForwardedFor(request, context);
	        //outsocket = socketFactory.createSocket();
	        
	        //InetAddress remoteAddress = InetAddress.getByName(reverseUrl.getTargetAddress().getHostName());
	        //InetSocketAddress remote = new InetSocketAddress(remoteAddress, reverseUrl.getTargetAddress().getPort());
	        //for 4.1
	        //socketFactory.connectSocket(outsocket, remote, null, builder.buildParams());
	        
	        HttpClient httpClient = new DefaultHttpClient(cm);

	        //for 4.0 @deprecated
	        //socketFactory.connectSocket(outsocket, 
	        //	reverseUrl.getTargetAddress().getHostName(),
	        //	reverseUrl.getTargetAddress().getPort(),
	        //	null, -1, builder.buildParams());
	        
	        //conn.bind(outsocket, builder.buildParams());
	        
	        //if (LOG.isTraceEnabled()) {
		    //    LOG.trace("Outgoing connection to " + outsocket.getInetAddress());
		    //    LOG.trace("request: " + request);
		    //}

	        ReverseHttpRequest targetRequest = null;
	        if (request instanceof HttpEntityEnclosingRequest) {
	        	targetRequest = new ReverseHttpEntityEnclosingRequest(request, context, reverseUrl);
	        } else {
	        	targetRequest = new ReverseHttpRequest(request, context, reverseUrl);
	        }
	        reverseUrl.countUp();
	        
	        //forward remote user.
	        ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
	        try {
	        	HttpHost host = new HttpHost(reverseUrl.getTargetAddress().getHostName());
		        //httpexecutor.preProcess(targetRequest, httpproc, context);
		        //HttpResponse targetResponse = httpexecutor.execute(targetRequest, conn, context);
	        	HttpResponse targetResponse = httpClient.execute(host, targetRequest, context);
		        //httpexecutor.postProcess(targetResponse, httpproc, context);
		        return targetResponse;
	        } finally {
		        reverseUrl.countDown();
		        if (LOG.isDebugEnabled()) {
		        	LOG.debug(">> "+ reverseUrl.getReverse() + ", connections=" + reverseUrl.getActiveConnections());
		        }
	        }
		} catch (SocketException e) {
			throw new ServiceUnavailableException(
				BasicHttpStatus.SC_GATEWAY_TIMEOUT.getReasonPhrase()
				+ " URL=" + reverseUrl.getReverse());
		} catch (RuntimeException e) {
			handleException(request, response, e);
			return response;
		} catch (Exception e) {
			handleException(request, response, e);
			return response;
		} finally {
			//IOUtils.close(conn);
			//IOUtils.close(outsocket);
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
}
