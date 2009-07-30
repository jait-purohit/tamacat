/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.util.Properties;


import org.apache.velocity.app.Velocity;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

public class ServerConfig {

    static final int DEFAULT_PORT = 80;
    static final int MAX_SERVER_THREADS = 5;
    
	private Properties props;
	
	public ServerConfig() {
		this(PropertyUtils.getProperties("server.properties"));
	}
	
	public ServerConfig(Properties props) {
		this.props = props;
		try {
			Velocity.init(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getPort() {
		return getParam("Port", DEFAULT_PORT);
	}
	
	public int getMaxThreads() {
		return getParam("MaxServerThreads", MAX_SERVER_THREADS);
    }
	
	public int getSocketTimeout() {
		return getParam("ServerSocketTimeout", 30000);
	}

	public int getSocketBufferSize() {
		return getParam("ServerSocketBufferSize", (8*1024));
	}
	
	public boolean useHttps() {
		return getParam("https", "true").equalsIgnoreCase("true");
	}
	
	public String getParam(String name) {
		return props.getProperty(name);
	}
	
	public void setParam(String name, String value) {
		props.setProperty(name, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getParam(String name, T defaultValue) {
		String value = props.getProperty(name);
		if (defaultValue == null) return (T) value;
    	return StringUtils.parse(value, defaultValue);
	}
}
