package org.tamacat.httpd.mock;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class HttpObjectFactory {

	public static HttpRequest createHttpRequest(String method, String uri) {
		return new BasicHttpRequest(method, uri);
	}
	
	public static HttpResponse createHttpResponse(
			String protocol, int status, String reason) {
		StatusLine statusLine = new BasicStatusLine(
			new ProtocolVersion(protocol,1,1), status, reason);
		return new BasicHttpResponse(statusLine);
	}
}
