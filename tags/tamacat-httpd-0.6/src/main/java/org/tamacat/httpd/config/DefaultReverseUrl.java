/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.InetSocketAddress;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.utils.CloneUtils;

/**
 * <p>The default implements of {@link ReverseUrl}.
 */
public class DefaultReverseUrl implements ReverseUrl, Cloneable {

	private ServiceUrl serviceUrl;	
	private URL reverseUrl;
	private InetSocketAddress targetAddress;
	
	/**
	 * <p>Constructs with the specified {@link ServiceUrl}. 
	 * @param serviceUrl
	 */
	public DefaultReverseUrl(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	@Override
	public ServiceUrl getServiceUrl() {
		return serviceUrl;
	}
	
	@Override
	public URL getHost() {
		return serviceUrl.getHost();
	}
	
	@Override
	public void setHost(URL host) {
		serviceUrl.setHost(host);
	}

	@Override
	public URL getReverse() {
		return reverseUrl;
	}

	@Override
	public URL getReverseUrl(String path) {
		String p = serviceUrl.getPath();
		if (path != null && p != null && path.startsWith(p)) {
    		String distUrl = path.replaceFirst(serviceUrl.getPath(), reverseUrl.getPath());
    		try {
    			int port = reverseUrl.getPort();
    			//if (port == -1) port = reverseUrl.getDefaultPort();
    			URL convertUrl = new URL(reverseUrl.getProtocol(),
                    reverseUrl.getHost(), port, distUrl);
    			return convertUrl;
    		} catch (MalformedURLException e) {}
    	}
		return null;
	}

	@Override
	public InetSocketAddress getTargetAddress() {
		return targetAddress;
	}

	@Override
	public void setReverse(URL reverseUrl) {
		this.reverseUrl = reverseUrl;
		int port = reverseUrl.getPort();
		if (port == -1) port = reverseUrl.getDefaultPort();
		targetAddress = new InetSocketAddress(reverseUrl.getHost(), port);
	}

	@Override
	public String getConvertRequestedUrl(String path) {
		URL host = getHost();
    	if (path != null && host != null) {
    		return path.replaceFirst(
    				reverseUrl.toString(),
    				host.toString()
    		);
    	} else {
    		return path;
    	}
    }
	
    @Override
    public DefaultReverseUrl clone() throws CloneNotSupportedException {
    	DefaultReverseUrl clone = 
            (DefaultReverseUrl) super.clone();
        if (this.serviceUrl != null) {
            clone.serviceUrl = (ServiceUrl) CloneUtils.clone(this.serviceUrl);
        }
        return clone;
    }
}
