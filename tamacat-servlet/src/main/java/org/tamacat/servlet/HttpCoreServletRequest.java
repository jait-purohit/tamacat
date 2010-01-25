package org.tamacat.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public interface HttpCoreServletRequest extends HttpServletRequest {

	HttpRequest getHttpRequest();
	HttpContext getHttpContext();
	
	void setParameter(String name, String... values);
	
	void addUserInRole(String role);
}
