/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.BufferingNHttpEntity;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.NHttpResponseTrigger;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.HttpFilter;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.page.VelocityErrorPage;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>This class is implements of the abstraction of {@link HttpHandler} interface.
 */
public abstract class AbstractNHttpHandler implements NHttpHandler {
	
    static final Log LOG = LogFactory.getLog(AbstractNHttpHandler.class);
    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	static Properties mimeTypes;
	private static String serverHome;
	
	/*
	 * 1. using first mime-types.properties in CLASSPATH. (optional)
	 * 2. using org/tamacat/httpd/mime-types.properties} in jar archive.
	 */
    static {
    	try {
    		mimeTypes = PropertyUtils.getProperties("mime-types.properties");
    	} catch (Exception e) {
    		//use default mime-types.
    		mimeTypes = PropertyUtils.getProperties("org/tamacat/httpd/mime-types.properties");
    	}
		try {
			serverHome = System.getProperty("server.home");
			if (serverHome == null) serverHome = System.getProperty("user.dir");
			File home = new File(serverHome);
			serverHome = home.getCanonicalPath();
		} catch (Exception e) {
		}
    }

	protected VelocityErrorPage errorPage;
    protected ServiceUrl serviceUrl;
    protected String docsRoot;
    
    protected List<HttpFilter> filters = new ArrayList<HttpFilter>();
    protected List<RequestFilter> requestFilters = new ArrayList<RequestFilter>();
    protected List<ResponseFilter> responseFilters = new ArrayList<ResponseFilter>();
    protected ClassLoader loader;

	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	this.serviceUrl = serviceUrl;
    	for (HttpFilter filter : filters) {
    		filter.init(serviceUrl);
    	}
    	Properties props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
    	errorPage = new VelocityErrorPage(props);
    }
    
	@Override
	public void setHttpFilter(HttpFilter filter) {
		filters.add(filter);
		if (filter instanceof RequestFilter) {
			requestFilters.add((RequestFilter)filter);
		}
		if (filter instanceof ResponseFilter) {
			responseFilters.add((ResponseFilter) filter);
		}
	}

	/**
	 * <p>Set the path of document root.
	 * @param docsRoot
	 */
	public void setDocsRoot(String docsRoot) {
		this.docsRoot = docsRoot.replace("${server.home}", serverHome).replace("\\", "/");
	}
	
	@Override
    public void handle(final HttpRequest request, final HttpResponse response,
            final NHttpResponseTrigger trigger, final HttpContext context)
        throws HttpException, IOException {
//        new Thread() {       
//            @Override
//            public void run() {
				try {
					for (RequestFilter filter : requestFilters) {
						filter.doFilter(request, response, context);
					}
					doRequest(request, response, context);
				} catch (Exception e) {
					handleException(request, response, e);
					if (e instanceof org.apache.http.HttpException) {
						trigger.handleException((org.apache.http.HttpException)e);
					}
				} finally {
					for (ResponseFilter filter : responseFilters) {
						filter.afterResponse(request, response, context);
					}
					// Submit response immediately for simplicity
			        trigger.submitResponse(response);
				}
//            }
//        }.start();
	}
	
	/**
	 * <p>When the exception is generated by processing {@link handleRequest},
	 *  this method is executed. 
	 *  
	 * @param request
	 * @param response
	 * @param e
	 */
	protected void handleException(HttpRequest request, HttpResponse response, Exception e) {
		String html = null;
		if (e instanceof HttpException) {
			html = errorPage.getErrorPage(request, response, (HttpException)e);
		} else {
			e.printStackTrace();
			html = errorPage.getErrorPage(request, response,
					new ServiceUnavailableException(e));
		}
		HttpEntity entity = getEntity(html);
		ResponseUtils.setEntity(response, entity);
	}
	
	/**
	 * <p>Handling the request, this method is executed after {@link RequestFilter}.
	 * @see {@link executeRequestFilter}
	 * @param request
	 * @param response
	 * @param context
	 * @throws HttpException
	 * @throws IOException
	 */
	protected abstract void doRequest(
				HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException;

	@Override
    public ConsumingNHttpEntity entityRequest(
            HttpEntityEnclosingRequest request, 
            HttpContext context) throws HttpException, IOException {
        // Buffer imcoming content in memory for simplicity 
        return new BufferingNHttpEntity(request.getEntity(),
                new HeapByteBufferAllocator());
    }
	
	/**
	 * <p>The entity is acquired based on the string. 
	 * @param html
	 * @return {@link HttpEntity}
	 */
	protected abstract HttpEntity getEntity(String html);
	
	/**
	 * <p>The entity is acquired based on the file. 
	 * @param file
	 * @return {@link HttpEntity}
	 */
	protected abstract HttpEntity getFileEntity(File file);
	
	/**
	 * <p>The contents type is acquired from the extension. <br>
	 * The correspondence of the extension and the contents type is
	 *  acquired from the {@code mime-types.properties} file. <br>
	 * When there is no file and the extension cannot be acquired,
	 * an {@link DEFAULT_CONTENT_TYPE} is returned. 
	 * @param file
	 * @return contents type
	 */
    protected String getContentType(File file) {
    	if (file == null) return DEFAULT_CONTENT_TYPE;
    	String fileName = file.getName();
    	String ext = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
    	String contentType =  mimeTypes.getProperty(ext.toLowerCase());
    	return StringUtils.isNotEmpty(contentType)? contentType : DEFAULT_CONTENT_TYPE;
    }
    
    /**
     * <p>Returns the decoded URI.
     * @param uri
     * @return default decoding is UTF-8.
     */
    protected String getDecodeUri(String uri) {
    	try {
    		return URLDecoder.decode(uri, "UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		return uri;
    	}
    }
    
    public void setClassLoader(ClassLoader loader) {
    	this.loader = loader;
    }
    
    public ClassLoader getClassLoader() {
    	return loader != null ? loader : getClass().getClassLoader();
    }
}