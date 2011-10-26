package org.tamacat.httpd.auth;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface SingleSignOn {

	void sign(String remoteUser, HttpRequest request, HttpResponse response, HttpContext context);
	
	boolean isSigned(HttpRequest request, HttpContext context);
	
	String getSignedUser(HttpRequest request, HttpContext context);
}
