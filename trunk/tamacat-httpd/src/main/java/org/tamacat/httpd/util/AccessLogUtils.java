/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * <p>Access log utility.<br>
 * 
 * Log category : Access
 * 
 * <p>logging for:
 * <ul>
 *  <li>Remote IP address</li>
 *  <li>Access time</li>
 *  <li>Remote Username</li>
 *  <li>URL</li>
 *  <li>HTTP status code</li>
 *  <li>Content-Length(size)</li>
 *  <li>Response time</li>
 * </ul>
 */
public class AccessLogUtils {
	
	static final Log ACCESS_LOG = LogFactory.getLog("Access");
    static final DiagnosticContext DC = LogFactory.getDiagnosticContext(ACCESS_LOG);
	
	/**
	 * Write the access log.
	 * @param context Before set the remote IP address and username.
	 * @param time Response time
	 */
	static
	  public void writeAccessLog(
			  HttpContext context, long time) {
		HttpRequest request =(HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		HttpResponse response = (HttpResponse) context.getAttribute(ExecutionContext.HTTP_RESPONSE);
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        String uri = request.getRequestLine().getUri();
        int statusCode = response.getStatusLine().getStatusCode();
        String reasonPhrase = response.getStatusLine().getReasonPhrase();
        String proto = request.getProtocolVersion().toString();
        String ip = RequestUtils.getRemoteIPAddress(context);
        if (ip == null) ip = ""; 
        String remoteUser = (String) context.getAttribute(AuthComponent.REMOTE_USER_KEY);
        if (StringUtils.isEmpty(remoteUser)) remoteUser = "-";
        HttpEntity entity = response.getEntity();
        long size = entity != null ? entity.getContentLength() : 0;
        if (size == -1) {
        	Header h = response.getFirstHeader(HTTP.CONTENT_LEN);
        	if (h != null) {
        		size = StringUtils.parse(h.getValue(), -1L);
        	}
        }
        DC.setMappedContext("ip", ip);
        DC.setMappedContext("user", remoteUser);
        try {
        	ACCESS_LOG.info(method + " " + uri + " " + proto +" " + statusCode
        		+ " [" + reasonPhrase + "] " + size + " (" + time + "ms)");
        } finally {
        	DC.remove("ip");
        	DC.remove("user");
        }
	}
}
