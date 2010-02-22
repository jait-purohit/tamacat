package org.tamacat.httpd.filter;

import org.tamacat.httpd.config.ServiceUrl;

public interface HttpFilter {
	
	void init(ServiceUrl serviceUrl);
}
