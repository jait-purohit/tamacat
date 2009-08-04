/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.lb;

import java.util.ArrayList;
import java.util.List;

import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;

public class LbRoundRobinServiceUrl extends ServiceUrl {
	
	private List<ReverseUrl> reverseUrls = new ArrayList<ReverseUrl>();
	private int next;
	
	public LbRoundRobinServiceUrl(ServerConfig serverConfig) {
		super(serverConfig);
	}
	
	@Override
	public void setReverseUrl(ReverseUrl reverseUrl) {
		this.reverseUrls.add(reverseUrl);
	}
	
	@Override
	public ReverseUrl getReverseUrl() {
		if (next >= reverseUrls.size()) {
			next = 0;
		}
		ReverseUrl reverseUrl = reverseUrls.get(next);
		next++;
		return reverseUrl;
	}

}
