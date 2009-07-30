/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.UnsupportedEncodingException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.page.VelocityErrorPage;
import org.tamacat.httpd.util.AccessLogUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class DefaultHttpService extends HttpService {

	static final Log LOG = LogFactory.getLog(DefaultHttpService.class);

    static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
	private HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();
    private HttpRequestHandlerResolver handlerResolver;
    
	public DefaultHttpService() {
		super(new BasicHttpProcessor(),
				new DefaultConnectionReuseStrategy(), 
        	new DefaultHttpResponseFactory()
		);
		setDefaultHttpResponseInterceptors();
	}
	
    public DefaultHttpService(
            final HttpProcessor proc,
            final ConnectionReuseStrategy connStrategy,
            final HttpResponseFactory responseFactory) {
    	super(proc, connStrategy, responseFactory);
    	setDefaultHttpResponseInterceptors();
    }

    public void setHandlerResolver(final HttpRequestHandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }
    
	private void setDefaultHttpResponseInterceptors() {
		procBuilder.addInterceptor(new ResponseDate());
		procBuilder.addInterceptor(new ResponseServer());
		procBuilder.addInterceptor(new ResponseContent());
		procBuilder.addInterceptor(new ResponseConnControl());

//        inhttpproc.addInterceptor(new HttpRequestInterceptor() {
//            public void process(
//                    HttpRequest request, 
//                    HttpContext context) throws HttpException, IOException {
//                if (request instanceof HttpEntityEnclosingRequest) {
//                	
//                	HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
//                	//System.out.println(BasicHttpEntity(entity);
//               		//List<NameValuePair> paramList = URLEncodedUtils.parse(entity);
//               		//System.out.println("params=" + paramList.size());
//            	}
//            }
//        });
	}
	
	public void setHttpResponseInterceptor(HttpResponseInterceptor interceptor) {
		procBuilder.addInterceptor(interceptor);
	}
	
	@Override
	//handleRequest() -> doService()->service()
	public void doService(HttpRequest request, HttpResponse response, HttpContext context) {
		long start = System.currentTimeMillis();
		try {
			LOG.trace("doService() >> " + request.getRequestLine().getUri());
			HttpRequestHandler handler = null;
			if (handlerResolver != null) {
				handler = handlerResolver.lookup(request.getRequestLine().getUri());
				LOG.trace("handler: " + handler);
			}
	        if (handler != null) {
	        	handler.handle(request, response, context);
	        } else {
	            throw new NotFoundException();
	        }
		} catch (Exception e) {
			if (e instanceof org.tamacat.httpd.exception.HttpException) {
				handleException(request, response,
						(org.tamacat.httpd.exception.HttpException)e);
			} else {
				handleException(request, response, 
					new ServiceUnavailableException());
			}
		} finally {
			AccessLogUtils.writeAccessLog(request, response, context, 
					System.currentTimeMillis() - start);
		}
	}

	protected void handleException(HttpRequest request, HttpResponse response,
			org.tamacat.httpd.exception.HttpException e) {
		VelocityErrorPage page = new VelocityErrorPage();
		String html = page.getErrorPage(request, response, e);
		response.setEntity(getEntity(html));
	}
	
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
}
