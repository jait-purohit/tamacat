package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.jmx.BasicCounter;
import org.tamacat.httpd.jmx.URLBasicCounter;

public class PerformanceCounterFilter implements RequestFilter, ResponseFilter {

	private static final URLBasicCounter urlCounter = new URLBasicCounter();

	/**
	 * <p>Set the base ObjectName for JMX.
	 * ObjectName is append the URL path.<br>
	 * default: "org.tamacat.httpd:type=URL/${path}"
	 * @param objectName
	 */
	public void setObjectName(String objectName) {
		urlCounter.setObjectName(objectName);
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		BasicCounter counter = urlCounter.getCounter(serviceUrl.getPath());
		if (counter != null) counter.countUp();
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		urlCounter.register(serviceUrl.getPath());
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		BasicCounter counter = urlCounter.getCounter(serviceUrl.getPath());
		if (counter != null) counter.countDown();
	}
}
