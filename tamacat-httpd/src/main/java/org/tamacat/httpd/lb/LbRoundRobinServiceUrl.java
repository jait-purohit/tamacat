/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.lb;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.monitor.HttpMonitor;
import org.tamacat.httpd.monitor.MonitorConfig;
import org.tamacat.httpd.util.DefaultThreadFactory;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.ResourceNotFoundException;
import org.tamacat.util.StringUtils;

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
 * 
 * <pre>ex. monitor.properties
 * {@code
 * default.url=check.html
 * default.interval=15000
 * default.timeout=5000
 * 
 * /lb/.url=test/check.html
 * /lb/.interval=60000
 * /lb/.timeout=15000
 * }
 * </pre>
 */
public class LbRoundRobinServiceUrl extends ServiceUrl
		implements HealthCheckSupport<ReverseUrl> {
	
	static final Log LOG = LogFactory.getLog(LbRoundRobinServiceUrl.class);
	private static final String MONITOR_PROPERTIES = "monitor.properties";
	private static final String DEFAULT_URL_KEY = "default.url";
	private static final String DEFAULT_INTERVAL_KEY = "default.interval";
	private static final String DEFAULT_TIMEOUT_KEY = "default.timeout";
	
	private List<ReverseUrl> reverseUrls = new ArrayList<ReverseUrl>();
	private int next;
	
	private Properties monitorProps;
	private int defaultInterval = 15000;
	private int defaultTimeout = 5000;
	private String defaultCheckUrl = "check.html";
	
	public LbRoundRobinServiceUrl() {
		loadMonitorConfig();
	}
	
	public LbRoundRobinServiceUrl(ServerConfig serverConfig) {
		super(serverConfig);
		loadMonitorConfig();
	}
	
	protected void loadMonitorConfig() {
		try {
			monitorProps = PropertyUtils.getProperties(MONITOR_PROPERTIES);
			defaultCheckUrl = monitorProps.getProperty(DEFAULT_URL_KEY, defaultCheckUrl);
			defaultInterval = StringUtils.parse(
				monitorProps.getProperty(DEFAULT_INTERVAL_KEY), defaultInterval);
			defaultTimeout = StringUtils.parse(
				monitorProps.getProperty(DEFAULT_TIMEOUT_KEY), defaultTimeout);
		} catch (ResourceNotFoundException e) {
			monitorProps = new Properties();
			monitorProps.setProperty(DEFAULT_URL_KEY, defaultCheckUrl);
			monitorProps.setProperty(DEFAULT_INTERVAL_KEY, String.valueOf(defaultInterval));
			monitorProps.setProperty(DEFAULT_TIMEOUT_KEY, String.valueOf(defaultTimeout));
		}
	}
	
	public List<ReverseUrl> getReverseUrls() {
		return reverseUrls;
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
		LOG.trace("add: " + target.getReverse());
		reverseUrls.add(target);
	}

	@Override
	public void removeTarget(ReverseUrl target) {
		LOG.trace("del: " + target.getReverse());
		reverseUrls.remove(target);
	}

	@Override
	public void startHealthCheck() {
		for (ReverseUrl url : reverseUrls) {
			HttpMonitor<ReverseUrl> monitor = new HttpMonitor<ReverseUrl>();
			monitor.setHealthCheckTarget(this);
			monitor.setMonitorConfig(getMonitorConfig(url));
			monitor.setTarget(url);
			new DefaultThreadFactory("Monitor").newThread(monitor).start();
			monitor.startMonitor();
		}
	}
	
	MonitorConfig getMonitorConfig(ReverseUrl url) {
		String key = url.getServiceUrl().getPath();
		if (key == null) {
			key = "default";
		}
		MonitorConfig config = new MonitorConfig();
		String checkUrl = monitorProps.getProperty(key + ".url");
		URL u = url.getReverse();
		if (checkUrl == null) {
			checkUrl = defaultCheckUrl;
		}
		if (checkUrl.startsWith("http://")==false
			&& checkUrl.startsWith("https://")==false) {
			checkUrl = u != null ? u.toString() + checkUrl : checkUrl;
		}
		config.setUrl(checkUrl);
		config.setInterval(StringUtils.parse(
			monitorProps.getProperty(key + ".interval"), defaultInterval)
		);
		config.setTimeout(StringUtils.parse(
			monitorProps.getProperty(key + ".timeout"), defaultTimeout)
		);
		return config;
	}
}
