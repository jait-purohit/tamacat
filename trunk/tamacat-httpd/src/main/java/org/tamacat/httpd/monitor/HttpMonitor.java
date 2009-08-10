/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.monitor;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.tamacat.httpd.config.MonitorConfig;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>Thread of HTTP Monitor for back end server.
 *
 * @param <T> target of {@code HealthCheckSupport}.
 */
public class HttpMonitor<T> implements Runnable {

	static final Log LOG = LogFactory.getLog("MonitorLog");
	
	private MonitorConfig config;
	private T target;
	private HealthCheckSupport<T> healthCheckTarget;
	private boolean isNormal = true;
	private boolean isStarted;
	
	public void setHealthCheckTarget(
			HealthCheckSupport<T> healthCheckTarget) {
		this.healthCheckTarget = healthCheckTarget;
	}
	
	public void setTarget(T target) {
		this.target = target;
	}
	
	public void setMonitorConfig(MonitorConfig config) {
		this.config = config;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (isStarted) {
					synchronized(healthCheckTarget) {
						boolean result = check();
						if (isNormal == true && result == false) {
							healthCheckTarget.removeTarget(target);
							isNormal = false;
							LOG.info("check: " + config.getUrl() + " is down.");
						} else if (isNormal == false && result == true){
							healthCheckTarget.addTarget(target);
							isNormal = true;
							LOG.info("check: " + config.getUrl() + " is up.");
						}
					}
				}
				Thread.sleep(config.getInterval());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				isStarted = false;
			}
		}
	}
	
	protected boolean check() {
		if (config == null) return true;
		HttpClient client= new DefaultHttpClient();
		boolean result = false;
		try {
			HttpResponse response = client.execute(new HttpGet(config.getUrl()));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = true;
			}
		} catch (Exception e) {
			if (isNormal) LOG.warn(e.getMessage());
		}
		return result;
	}
	
	public boolean isNormal() {
		return isNormal;
	}
	
	public void startMonitor() {
		isStarted = true;
	}
	
	public void stopMonitor() {
		isStarted = false;
	}
}
