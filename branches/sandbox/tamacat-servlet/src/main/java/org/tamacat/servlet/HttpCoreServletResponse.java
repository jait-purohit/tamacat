package org.tamacat.servlet;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface HttpCoreServletResponse extends HttpServletResponse {

	HttpResponse getHttpResponse();
	
	HttpContext getHttpContext();
}
