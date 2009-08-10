/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.Header;

import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.protocol.HTTP;

import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>Client side request for reverse proxy.
 * (Implements HttpRequest)
 */
public class ReverseHttpRequest extends BasicHttpRequest {
	static final Log LOG = LogFactory.getLog(ReverseHttpRequest.class);

	protected ReverseUrl reverseUrl;
	
	public ReverseHttpRequest(RequestLine line, ReverseUrl reverseUrl) {
		super(line);
		this.reverseUrl = reverseUrl;
	}
	
	public ReverseHttpRequest(HttpRequest request, ReverseUrl reverseUrl) {
		super(new BasicRequestLine(
	    		request.getRequestLine().getMethod(),
	    		reverseUrl.getReverseUrl(request.getRequestLine().getUri()).toString(),
	    		request.getRequestLine().getProtocolVersion())
		);
		this.reverseUrl = reverseUrl;
		setRequest(request);
	}
	
	public void setRequest(HttpRequest request) {
        rewriteHostHeader(request);
        
        setHeaders(request.getAllHeaders());
        setParams(request.getParams());
        // Remove hop-by-hop headers
        removeHeaders(HTTP.CONTENT_LEN);
        removeHeaders(HTTP.TRANSFER_ENCODING);
        removeHeaders(HTTP.CONN_DIRECTIVE);
        removeHeaders("Keep-Alive");
        removeHeaders("Proxy-Authenticate");
        removeHeaders("Proxy-Authorization");
        removeHeaders("TE");
        removeHeaders("Trailers");
        removeHeaders("Upgrade");
        removeHeaders("Range");
	}
	
	//rewrite Host Header
	private void rewriteHostHeader(HttpRequest request) {
        Header[] hostHeaders = request.getHeaders(HTTP.TARGET_HOST);
        for (Header hostHeader : hostHeaders) {
        	String value = hostHeader.getValue();
        	String before = reverseUrl.getHost().getHost();
        	int beforePort = reverseUrl.getHost().getPort();
        	if (beforePort != 80 && beforePort > 0) {
        		before = before + ":" + beforePort;
        	}
        	String after = reverseUrl.getReverse().getHost();
        	int afterPort = reverseUrl.getReverse().getPort();
        	if (afterPort != 80 && afterPort > 0) {
        		after = after + ":" + afterPort;
        	}
        	String newValue = value.replace(before, after);
        	LOG.trace("Host: " + value + " >> " + newValue);
        	Header newHeader = new BasicHeader(hostHeader.getName(), newValue);
        	request.removeHeader(hostHeader);
        	request.addHeader(newHeader);
        }	
	}
	
//    @Override
//    public ReverseHttpRequest clone() throws CloneNotSupportedException {
//    	ReverseHttpRequest clone = 
//            (ReverseHttpRequest) super.clone();
//        if (this.reverseUrl != null) {
//            clone.reverseUrl = (ReverseUrl) CloneUtils.clone(this.reverseUrl);
//        }
//        return clone;
//    }
}
