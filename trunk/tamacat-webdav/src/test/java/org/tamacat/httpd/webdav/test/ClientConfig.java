/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav.test;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

public class ClientConfig {

	Properties props;
	public ClientConfig(String fileName) {
		props = PropertyUtils.getProperties(fileName);
	}

	public String getProxyHost() {
		return props.getProperty("proxyHost");
	}

	public int getProxyPort() {
		return StringUtils.parse(props.getProperty("proxyPort"), -1);
	}

	public String getProxyUser() {
		return props.getProperty("proxyUser");
	}

	public String getProxyPassword() {
		return props.getProperty("proxyPassword");
	}

	class HttpProxySelector extends ProxySelector {
		final String proxyHost;
		final int proxyPort;
		public HttpProxySelector(String proxyHost, int proxyPort) {
			this.proxyHost = proxyHost;
			this.proxyPort = proxyPort;
		}

		@Override
		public List<Proxy> select(URI uri) {
			return Arrays.asList(new Proxy(Proxy.Type.HTTP,
				new InetSocketAddress(proxyHost, proxyPort))
			);
		}

		@Override
		public void connectFailed(URI uri, SocketAddress sa, IOException e) {
			throw new RuntimeException(e);
		}

	}

	void setProxy() {
		final String proxyHost = getProxyHost();
		final int proxyPort = getProxyPort();
		if (StringUtils.isNotEmpty(proxyHost) && proxyPort > 0) {
			ProxySelector.setDefault(new HttpProxySelector(proxyHost, proxyPort));

			final String proxyUser = getProxyUser();
			final String proxyPassword = getProxyPassword();
			if (StringUtils.isNotEmpty(proxyUser) && StringUtils.isNotEmpty(proxyPassword)) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						if (getRequestorType() == RequestorType.PROXY) {
							return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
						} else {
							return super.getPasswordAuthentication();
						}
					}
				});
			}
		}
	}
}
