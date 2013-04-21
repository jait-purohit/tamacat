package org.tamacat.httpd.core;

import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.HttpContext;

public class ReverseProxyConnectionReuseStrategy extends
		DefaultConnectionReuseStrategy {

	boolean disabledKeepAlive;
	
    public boolean isDisabledKeepAlive() {
		return disabledKeepAlive;
	}

	public void setDisabledKeepAlive(boolean disabledKeepAlive) {
		this.disabledKeepAlive = disabledKeepAlive;
	}

	@Override
	public boolean keepAlive(final HttpResponse response,
            final HttpContext context) {
    	return disabledKeepAlive ? false : super.keepAlive(response, context);
    }
}
