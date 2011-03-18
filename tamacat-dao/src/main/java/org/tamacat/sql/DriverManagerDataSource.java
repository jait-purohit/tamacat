/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.tamacat.util.ClassUtils;

public class DriverManagerDataSource implements DataSource {
	
	private Class<?> driverClass;
	private Properties options = new Properties();
	private String url;
	
	public DriverManagerDataSource() {}
	
	public Class<?> getDriverClass() {
		return driverClass;
	}
	
	public void setDriverClass(Class<?> driverClass) {
		this.driverClass = driverClass;
	}
	
	public void setDriverClass(String className) {
		this.driverClass = ClassUtils.forName(className.trim());
	}
	
	public void setUrl(String url) {
		this.url = url != null? url.trim() : "";
	}
	
	public void setUsername(String username) {
		if (username != null) {
			options.setProperty("user", username);
		}
	}
	
	public void setPassword(String password) {
		if (password != null) {
			options.setProperty("password", password);
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		//return DriverManager.getConnection(url, options);
		return DriverManager.getDriver(url).connect(url, options);
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		setUsername(username);
		setPassword(password);
		return getConnection();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.equals(iface);
	}

	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (! DataSource.class.equals(iface)) {
			throw new SQLException(
				"DataSource of type [" + getClass().getName() + "] "
				+ "can only be unwrapped as [javax.sql.DataSource], "
				+ "not as [" + iface.getName());
		}
		return (T)this;
	}
}
