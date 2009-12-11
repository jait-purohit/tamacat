package org.tamacat.servlet;

import javax.servlet.http.HttpServletRequest;

public interface HttpCoreServletRequest extends HttpServletRequest {

	void setParameter(String name, String... values);
	
	void addUserInRole(String role);
}
