package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.util.AccessLogUtils;

public class AccessLogFilter implements RequestFilter, ResponseFilter {

	static final String START_TIME = "Response.startTime";
	static final String RESPONSE_TIME = "Response.responseTime";

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		long start = System.currentTimeMillis();
		context.setAttribute(START_TIME, start);
	}

	@Override
	public void init(ServiceUrl serviceUrl) {}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		long start = (Long) context.getAttribute(START_TIME);
		long time = System.currentTimeMillis() - start;
		context.setAttribute(RESPONSE_TIME, time);
		AccessLogUtils.writeAccessLog(request, response, context, time);
	}
}
