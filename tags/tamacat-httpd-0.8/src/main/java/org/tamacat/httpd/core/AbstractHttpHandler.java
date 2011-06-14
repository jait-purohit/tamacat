/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.HttpFilter;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.page.VelocityErrorPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>This class is implements of the abstraction of {@link HttpHandler} interface.
 */
public abstract class AbstractHttpHandler implements HttpHandler {
	
    static final Log LOG = LogFactory.getLog(AbstractHttpHandler.class);
    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	private static Properties mimeTypes;
	private static String serverHome;

	/*
	 * 1. using org/tamacat/httpd/mime-types.properties} in jar archive.
	 * 2. override or add the mime-types.properties in CLASSPATH. (optional)
	 */
    static {
    	mimeTypes = PropertyUtils.marge(
    			"org/tamacat/httpd/mime-types.properties", "mime-types.properties");
		try {
			serverHome = System.getProperty("server.home");
			if (serverHome == null) serverHome = System.getProperty("user.dir");
			File home = new File(serverHome);
			serverHome = home.getCanonicalPath();
		} catch (Exception e) {
			LOG.error(e);
		}
    }
	
	public static Properties getMimeTypes() {
		return mimeTypes;
	}
	
	protected VelocityErrorPage errorPage;
    protected ServiceUrl serviceUrl;
    protected String docsRoot;
    protected String encoding = "UTF-8";
    
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
			responseFilters.add((ResponseFilter)filter);
		}
	}
	
	/**
	 * <p>Set the path of document root.
	 * @param docsRoot
	 */
	public void setDocsRoot(String docsRoot) {
		this.docsRoot = docsRoot.replace("${server.home}", serverHome).replace("\\", "/");
	}
	
	/**
	 * <p>Set the character encoding. (default UTF-8)
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, 
			HttpContext context) {
		RequestUtils.setParameters(request, context, encoding);
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
			if (LOG.isWarnEnabled()) {
				LOG.warn(ExceptionUtils.getStackTrace(e, 500));
			}
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
    	String contentType =  getMimeTypes().getProperty(ext.toLowerCase());
    	return StringUtils.isNotEmpty(contentType)? contentType : DEFAULT_CONTENT_TYPE;
    }
    
    /**
     * <p>Returns the decoded URI.
     * @param uri
     * @return default decoding is UTF-8.
     */
    protected String getDecodeUri(String uri) {
    	try {
    		return URLDecoder.decode(uri, encoding);
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
