/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

public class AccessLogUtils {
	
	static final Log ACCESS_LOG = LogFactory.getLog("AccessLog");
    static final DiagnosticContext DC = LogFactory.getDiagnosticContext(ACCESS_LOG);
    
	static
	  public void writeAccessLog(
			  HttpRequest request, HttpResponse response,
			  HttpContext context, long time) {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        String uri = request.getRequestLine().getUri();
        int statusCode = response.getStatusLine().getStatusCode();
        String reasonPhrase = response.getStatusLine().getReasonPhrase();
        String proto = request.getProtocolVersion().toString();
        String ip = (String) context.getAttribute("remote_address"); //ReverseUtils.getRemoteIPAddress(context);
        if (ip == null) ip = ""; 
        String remoteUser = "";//.getRemoteUser();
        if (StringUtils.isEmpty(remoteUser)) remoteUser = "-";
        HttpEntity entity = response.getEntity();
        long size = entity != null ? entity.getContentLength() : 0;
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
