/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicRequestLine;
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
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.page.VelocityErrorPage;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class ReverseProxyHandler extends AbstractHttpHandler {
	
	static final Log LOG = LogFactory.getLog(ReverseProxyHandler.class);

    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	private HttpRequestExecutor httpexecutor;
    private HttpProcessor httpproc;
    private HttpParamsBuilder builder = new HttpParamsBuilder();
	private HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();

	public ReverseProxyHandler() {
		this.httpexecutor = new HttpRequestExecutor();
		setDefaultHttpRequestInterceptor();
	}
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	this.serviceUrl = serviceUrl;
    	builder.socketTimeout(serviceUrl.getServerConfig().getParam("BackEndSocketTimeout", 30000))
          .socketBufferSize(serviceUrl.getServerConfig().getParam("BackEndSocketBufferSize", (8*1024)));
    }
	

    @Override
    public void doRequest(
    		HttpRequest request, HttpResponse response, 
    		HttpContext context) throws HttpException, IOException {
    	
        // Access Backend server //
        HttpResponse targetResponse = forwardRequest(request, response, context);
        ReverseUrl reverseUrl = //serviceUrl.getReverseUrl();
        	(ReverseUrl) context.getAttribute("reverseUrl");
        ReverseUtils.copyHttpResponse(targetResponse, response);
        ReverseUtils.rewriteContentLocationHeader(response, reverseUrl);
        
        // Location Header convert. //
        ReverseUtils.rewriteLocationHeader(response, reverseUrl);
        
        // Set-Cookie Header convert. //
        ReverseUtils.rewriteSetCookieHeader(response, reverseUrl);	    
        response.setEntity(targetResponse.getEntity());
    }

	protected HttpResponse forwardRequest(
			HttpRequest request, HttpResponse response, HttpContext context) {
		this.httpproc = procBuilder.build();
		
        LOG.trace(">> Request URI: " + request.getRequestLine().getUri());

		try {
	        ReverseUrl reverseUrl = serviceUrl.getReverseUrl();
	        if (reverseUrl == null) {
	        	throw new ServiceUnavailableException("reverseUrl is null.");
	        }
	        context.setAttribute("reverseUrl", reverseUrl);
			Socket outsocket = new Socket(
					reverseUrl.getTargetAddress().getHostName(),
					reverseUrl.getTargetAddress().getPort());
			DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
	        conn.bind(outsocket, builder.build());
	        
	        if (LOG.isTraceEnabled()) {
		        LOG.trace("Outgoing connection to " + outsocket.getInetAddress());
		        LOG.trace("request: " + request);
		    }

	        ReverseHttpRequest targetRequest = null;
	        if (request instanceof HttpEntityEnclosingRequest) {
	        	targetRequest = new ReverseHttpEntityEnclosingRequest(request, reverseUrl);
	        } else {
	        	URL url = reverseUrl.getReverseUrl(request.getRequestLine().getUri());
		        if (url == null) {
		        	throw new NotFoundException("url is null.");
		        }
	        	BasicRequestLine line = new BasicRequestLine(
    		    		request.getRequestLine().getMethod(),
    		    		url.toString(),
    		    		request.getRequestLine().getProtocolVersion());
	        	targetRequest = new ReverseHttpRequest(line, reverseUrl);
	        	targetRequest.setRequest(request);
	        }
	        httpexecutor.preProcess(targetRequest, httpproc, context);
	        HttpResponse targetResponse = httpexecutor.execute(targetRequest, conn, context);
	        httpexecutor.postProcess(response, httpproc, context);
	        
	        return targetResponse;
		} catch (Exception e) {
			VelocityErrorPage page = new VelocityErrorPage();
			String html = null;
			if (e instanceof HttpException) {
				html = page.getErrorPage(request, response, (HttpException)e);
			} else {
				LOG.error(e.getMessage());
				e.printStackTrace(); //for debug
				html = page.getErrorPage(request, response, 
						new ServiceUnavailableException(e));
			}
			try {
				StringEntity entity = new StringEntity(html);
				entity.setContentType(DEFAULT_CONTENT_TYPE);
				response.setEntity(entity);
			} catch (UnsupportedEncodingException e1) {
			}
			return response;
		}
	}
	
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
	
	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, getContentType(file));
        return body;
	}
}
