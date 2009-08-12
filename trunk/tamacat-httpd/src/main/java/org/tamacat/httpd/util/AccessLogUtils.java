/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.net.InetAddress;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

public class AccessLogUtils {
	
	static final Log ACCESS_LOG = LogFactory.getLog("Access");
    static final DiagnosticContext DC = LogFactory.getDiagnosticContext(ACCESS_LOG);
	static final String REMOTE_ADDRESS = "remote_address";

	static
	  public void writeAccessLog(
			  HttpRequest request, HttpResponse response,
			  HttpContext context, long time) {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        String uri = request.getRequestLine().getUri();
        int statusCode = response.getStatusLine().getStatusCode();
        String reasonPhrase = response.getStatusLine().getReasonPhrase();
        String proto = request.getProtocolVersion().toString();
        String ip = getRemoteIPAddress(context);
        if (ip == null) ip = ""; 
        String remoteUser = (String) context.getAttribute(AuthComponent.REMOTE_USER_KEY);
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
	
	public static void setRemoteAddress(HttpContext context, InetAddress address) {
		context.setAttribute(REMOTE_ADDRESS, address);
	}
	
	public static String getRemoteIPAddress(HttpContext context) {
		InetAddress address= (InetAddress) context.getAttribute(REMOTE_ADDRESS);
		if (address != null) return address.getHostAddress();
		else return "";
	}
}
