package org.tamacat.servlet;

import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.exception.HttpException;

public class HttpCoreServletException extends HttpException {

	private static final long serialVersionUID = 1L;

	public HttpCoreServletException() {
		super(BasicHttpStatus.SC_INTERNAL_SERVER_ERROR);
	}

	public HttpCoreServletException(String message) {
		super(BasicHttpStatus.SC_INTERNAL_SERVER_ERROR, message);
	}
	
	public HttpCoreServletException(Throwable cause) {
		super(BasicHttpStatus.SC_INTERNAL_SERVER_ERROR, cause);
	}
}
