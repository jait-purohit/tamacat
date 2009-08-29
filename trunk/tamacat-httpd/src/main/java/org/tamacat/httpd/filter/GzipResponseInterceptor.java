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
import org.tamacat.util.IOUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>Server-side interceptor to handle Gzip-encoded responses.
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
        Header aeheader = request.getFirstHeader(ACCEPT_ENCODING);
        if (aeheader != null && useCompress(response.getFirstHeader(HTTP.CONTENT_TYPE))) {
	        HeaderElement[] codecs = aeheader.getElements();
	        for (int i=0; i<codecs.length; i++) {
	            if (codecs[i].getName().equalsIgnoreCase(GZIP_CODEC)) {
	            	GzipCompressingEntity entity = new GzipCompressingEntity(response.getEntity());
	                response.setEntity(entity);
	                response.setHeader(entity.getContentEncoding()); //Bugfix.
	                return;
	            }
            }
        }
	}
	
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
	
	boolean useCompress(Header contentType) {
		if (contentType == null) return false;
		String type = contentType.getValue();
		if (useAll || contentTypes.contains(type)) {
			return true;
		} else {
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
