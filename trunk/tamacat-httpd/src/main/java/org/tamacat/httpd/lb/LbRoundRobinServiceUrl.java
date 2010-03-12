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
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.monitor.HttpMonitor;
import org.tamacat.httpd.monitor.MonitorConfig;
import org.tamacat.httpd.util.DefaultThreadFactory;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>It is service URL setting of the round robin type load balancer.
 * 
 * <pre>ex. url-config.xml
 * {@code 
 * <?xml version="1.0" encoding="UTF-8"?> 
 * <service-config>
 *   <service host="http://localhost">
 *     <url path="/lb/" type="lb" handler="ReverseHandler">
 *       <reverse>http://localhost:8080/lb1/</reverse>
 *       <reverse>http://localhost:8088/lb2/</reverse>
 *     </url>
 *   </service>
 * </service-config>}
 * </pre>
 */
public class LbRoundRobinServiceUrl extends ServiceUrl
		implements HealthCheckSupport<ReverseUrl> {
	
	static final Log LOG = LogFactory.getLog(LbRoundRobinServiceUrl.class);

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
		return reverseUrl;
	}

	@Override
	public void addTarget(ReverseUrl target) {
		LOG.trace("add: "+target.getReverse());
		reverseUrls.add(target);
	}

	@Override
	public void removeTarget(ReverseUrl target) {
		LOG.trace("del: "+target.getReverse());
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
			new DefaultThreadFactory("Monitor").newThread(monitor).start();
			monitor.startMonitor();
		}
	}
}
