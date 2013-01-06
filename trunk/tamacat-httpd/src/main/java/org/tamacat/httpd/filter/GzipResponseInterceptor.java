/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.util.IOUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>Server-side interceptor to handle Gzip-encoded responses.<br>
 * The cord of the basis is Apache HttpComponents {@code ResponseGzipCompress.java}.</p>
 * 
 * <pre>Example:{@code components.xml}
 * {@code <bean id="gzip" class="org.tamacat.httpd.filter.GzipResponseInterceptor">
 *  <property name="contentType">
 *    <value>html,xml,css,javascript</value>
 *  </property>
 * </bean>
 * }</pre>
 * 
 * {@link http://svn.apache.org/repos/asf/httpcomponents/httpcore/trunk/contrib/src/main/java/org/apache/http/contrib/compress/ResponseGzipCompress.java}
 */
public class GzipResponseInterceptor implements HttpResponseInterceptor {

	private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP_CODEC = "gzip";

    private Set<String> contentTypes = new HashSet<String>();
    private boolean useAll = true;
    
	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
        Header aeheader = request != null ? request.getFirstHeader(ACCEPT_ENCODING) : null;
        if (aeheader != null && useCompress(response.getFirstHeader(HTTP.CONTENT_TYPE))) {
            String ua = HeaderUtils.getHeader(request, "User-Agent");        
            if (ua != null && ua.indexOf("MSIE 6.0") >= 0) {
            	return; //Skipped for IE6 bug(KB823386)
            }
	        HeaderElement[] codecs = aeheader.getElements();
	        for (int i=0; i<codecs.length; i++) {
	            if (codecs[i].getName().equalsIgnoreCase(GZIP_CODEC)) {
	            	GzipCompressingEntity entity = new GzipCompressingEntity(response.getEntity());
	                response.setEntity(entity);
	                response.setHeader(entity.getContentEncoding()); //Bugfix.
	                response.removeHeaders(HTTP.CONTENT_LEN);
	                response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
	                return;
	            }
            }
        }
	}
	
	/**
	 * <p>Set the content type of the gzip compression.<br>
	 * default are all content types to compressed.</p>
	 * <p>The {@code contentType} value is case insensitive,<br>
	 * and the white space of before and after is trimmed.</p>
	 * 
	 * <p>Examples: {@code contentType="html, css, javascript, xml" }
	 * <ul>
	 *   <li>text/html</li>
	 *   <li>text/css</li>
	 *   <li>text/javascript</li>
	 *   <li>application/xml</li>
	 *   <li>text/xml</li>
	 * </ul>
	 * @param contentType Comma Separated Value of content-type or sub types.
	 */
	public void setContentType(String contentType) {
		if (StringUtils.isNotEmpty(contentType)) {
			String[] csv = contentType.split(",");
			for (String t : csv) {
				contentTypes.add(t.trim().toLowerCase());
				useAll = false;
				String[] types = t.split(";")[0].split("/");
				if (types.length >= 2) {
					contentTypes.add(types[1].trim().toLowerCase());
				}
			}
		}
	}
	
	/**
	 * <p>Check for use compress contents.
	 * @param contentType
	 * @return true use compress.
	 */
	boolean useCompress(Header contentType) {
		if (contentType == null) return false;
		String type = contentType.getValue();
		if (useAll || contentTypes.contains(type)) {
			return true;
		} else {
			//Get the content sub type. (text/html; charset=UTF-8 -> html)
			String[] types = type != null ? type.split(";")[0].split("/") : new String[0];
			if (types.length >= 2 && contentTypes.contains(types[1])) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * <p>Wrapping entity that compresses content when {@link #writeTo writing}.
	 * {@link http://svn.apache.org/repos/asf/httpcomponents/httpcore/trunk/contrib/src/main/java/org/apache/http/contrib/compress/GzipCompressingEntity.java}
	 */
	static class GzipCompressingEntity extends HttpEntityWrapper {
		public GzipCompressingEntity(HttpEntity entity) {
	        super(entity);
	    }

		@Override
	    public Header getContentEncoding() {
	        return new BasicHeader(HTTP.CONTENT_ENCODING, GZIP_CODEC);
	    }
		
		@Override
	    public long getContentLength() {
	        return -1;
	    }
		
		@Override
	    public boolean isChunked() {
	        // force content chunking
	        return true;
	    }
		
		@Override
	    public void writeTo(OutputStream outstream) throws IOException {
	        if (outstream == null) {
	            throw new IllegalArgumentException("Output stream may not be null");
	        }
	        GZIPOutputStream gzip = new GZIPOutputStream(outstream);
	        try {
	        	InputStream in = wrappedEntity.getContent();
	        	byte[] tmp = new byte[2048];
	        	int l;
	        	while ((l = in.read(tmp)) != -1) {
	        		gzip.write(tmp, 0, l);
	        	}
	        	
	        } finally {
	        	IOUtils.close(gzip);
	        }
	    }
	}
}
