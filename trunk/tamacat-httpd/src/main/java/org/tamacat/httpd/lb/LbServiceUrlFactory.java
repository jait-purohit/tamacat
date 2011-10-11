/*
 * Copyright (c) 2011, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.lb;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;

public class LbServiceUrlFactory {

	public static LbHealthCheckServiceUrl getServiceUrl(ServiceUrl url) {
		ServerConfig serverConfig = url.getServerConfig();
		String method = url.getLoadBalancerMethod();
		if ("LeastConnection".equalsIgnoreCase(method)) {
			return new LbLeastConnectionServiceUrl(serverConfig);
		} else { //("RoundRobin".equalsIgnoreCase(method)) {
			return new LbRoundRobinServiceUrl(serverConfig);
		}
	}
}
