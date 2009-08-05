/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.lb;

import java.util.ArrayList;
import java.util.List;

import org.tamacat.httpd.config.MonitorConfig;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.monitor.HealthCheckSupport;
import org.tamacat.httpd.monitor.HttpMonitor;

public class LbRoundRobinServiceUrl extends ServiceUrl
		implements HealthCheckSupport<ReverseUrl> {
	
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
		ReverseUrl reverseUrl = null;
		synchronized (reverseUrls) {
			int size = reverseUrls.size();
			System.out.println(reverseUrls);
			if (size == 0) {
				throw new ServiceUnavailableException();
			} else if (size == 1) {
				reverseUrl = reverseUrls.get(0);
			} else if (size > 1) {
				if (next >= size) {
					next = 0;
				}
				reverseUrl = reverseUrls.get(next);
				next++;
			}
		}
		System.out.println("access: "+reverseUrl.getReverse());

		return reverseUrl;
	}

	@Override
	public void addTarget(ReverseUrl target) {
		System.out.println("add: "+target.getReverse());
		reverseUrls.add(target);
	}

	@Override
	public void removeTarget(ReverseUrl target) {
		System.out.println("del: "+target.getReverse());
		reverseUrls.remove(target);
	}

	@Override
	public void startHealthCheck() {
		for (ReverseUrl url : reverseUrls) {
			HttpMonitor<ReverseUrl> monitor = new HttpMonitor<ReverseUrl>();
			monitor.setHealthCheckTarget(this);
			MonitorConfig config = new MonitorConfig();
			config.setInterval(15000);
			config.setTimeout(5000);
			config.setUrl(url.getReverse().toString() + "check.html");
			monitor.setMonitorConfig(config);
			monitor.setTarget(url);
			new Thread(monitor).start();
			monitor.startMonitor();
		}
	}
}
