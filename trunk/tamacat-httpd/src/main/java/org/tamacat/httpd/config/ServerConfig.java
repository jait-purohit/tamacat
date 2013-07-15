/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.util.Properties;

import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>Server configurations.<br>
 * Default setting file is {@code server.properties} in CLASSPATH.
 */
public class ServerConfig {

	static final int DEFAULT_PORT = 80;
	static final int MAX_SERVER_THREADS = 5;

	private Properties props;

	/**
	 * <p>Use the "server.properties" file in CLASSPATH.
	 */
	public ServerConfig() {
		this(PropertyUtils.getProperties("server.properties"));
	}

	/**
	 * <p>Use the setting values from {@link Properties} object.
	 * @param props
	 */
	public ServerConfig(Properties props) {
		this.props = props;
		try {
			String home = System.getProperty("server.home");
			if (StringUtils.isNotEmpty(home)) {
				//Velocity.setProperty(VelocityEngine.RUNTIME_LOG, home + "/logs/velocity.log");
			}
			//Velocity.init(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>Returns the Listen port.
	 * @return Get the parameter value key of "Port",
	 * if value is null then returns {@code DEFAULT_PORT}.
	 */
	public int getPort() {
		return getParam("Port", DEFAULT_PORT);
	}

	/**
	 * <p>Returns the maximum server threads.
	 * @return Get the parameter value key of "MaxServerThreads",
	 * if value is null then returns {@code MAX_SERVER_THREADS}.
	 */
	public int getMaxThreads() {
		return getParam("MaxServerThreads", MAX_SERVER_THREADS);
	}

	/**
	 * <p>Returns the Socket timeout (ms).
	 * @return Get the parameter value key of "ServerSocketTimeout",
	 * if value is null then returns {@code 30000}.
	 */
	public int getSocketTimeout() {
		return getParam("ServerSocketTimeout", 30000);
	}

	/**
	 * <p>Returns the Connection timeout (ms).
	 * @return Get the parameter value key of "ConnectionTimeout",
	 * if value is null then returns {@code 30000}.
	 */
	public int getConnectionTimeout() {
		return getParam("ConnectionTimeout", 30000);
	}

	/**
	 * <p>Returns the Socket buffer size.
	 * @return Get the parameter value key of "ServerSocketBufferSize",
	 * if value is null then returns {@code (8*1024)}.
	 */
	public int getSocketBufferSize() {
		return getParam("ServerSocketBufferSize", (8*1024));
	}

	/**
	 * <p>If using the https, returns true.
	 * @return Get the parameter value key of "https",
	 * if value is null then returns {@code false}.
	 */
	public boolean useHttps() {
		return getParam("https", "false").equalsIgnoreCase("true");
	}

	/**
	 * <p>If using the https client authentication, returns true.
	 * @return Get the parameter value key of "https.clientAuth",
	 * if value is null then returns {@code false}.
	 */
	public boolean useClientAuth() {
		return getParam("https.clientAuth", "false").equalsIgnoreCase("true");
	}

	/**
	 * <p>Returns the value of parameter
	 * @param name parameter name.
	 * @return parameter value.
	 */
	public String getParam(String name) {
		return props.getProperty(name);
	}

	/**
	 * <p>Set the parameter.
	 * @param name
	 * @param value
	 */
	public void setParam(String name, String value) {
		props.setProperty(name, value);
	}

	/**
	 * Returns the parameter, if value is null then returns {@code defaultValue}.
	 * @param <T>
	 * @param name
	 * @param defaultValue
	 * @return if value is null then returns {@code defaultValue}.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParam(String name, T defaultValue) {
		String value = props.getProperty(name);
		if (defaultValue == null) return (T) value;
		return StringUtils.parse(value, defaultValue);
	}
}
