package org.tamacat.httpd.mock;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HttpObjectFactory {

	public static HttpRequest createHttpRequest(String method, String uri) {
		return new BasicHttpRequest(method, uri);
	}
	
	public static HttpResponse createHttpResponse(int status, String reason) {
		StatusLine statusLine = new BasicStatusLine(
			new ProtocolVersion("HTTP",1,1), status, reason);
		return new BasicHttpResponse(statusLine);
	}
	
	public static HttpContext createHttpContext() {
		return new BasicHttpContext();
	}
}
