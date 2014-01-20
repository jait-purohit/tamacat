/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebDavClient_test {

	static final Log LOG = LogFactory.getLog(WebDavClient_test.class);

	public static final String XML = "httpd.xml";

	public static void main(String[] args) {
		String config = args.length > 0 ? args[0] : XML;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		HttpEngine server = di.getBean("server", HttpEngine.class);
		Thread t = new Thread(server);
		t.start();

		String file = "http://localhost:8080/test.txt";

		put(file, new StringEntity("test1234","UTF-8"));

		//delete(file);

		System.exit(0);
	}

	static void put(String file, HttpEntity entity) {
		HttpPut request = new HttpPut(file);
		request.setEntity(entity);

		try (CloseableHttpClient client = HttpClients.custom().build()) {
			HttpResponse response = client.execute(request);
			LOG.info("------------------------------------------------------");
			LOG.info(request.getRequestLine());

			LOG.info(response.getStatusLine());
			LOG.info("------------------------------------------------------");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	static void delete(String file) {
		HttpDelete request = new HttpDelete(file);
		try (CloseableHttpClient client = HttpClients.custom().build()) {
			HttpResponse response = client.execute(request);
			LOG.info("------------------------------------------------------");
			LOG.info(request.getRequestLine());

			LOG.info(response.getStatusLine());
			LOG.info("------------------------------------------------------");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
}
