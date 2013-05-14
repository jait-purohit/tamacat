/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;

/**
 * <p>It is the start class of the http server.
 * The component setting in {@code components.xml}.
 */
public class Httpd {

	public static final String XML = "httpd.xml";
	private static final String DEFAULT_SERVER_KEY = "server";
	
	/**
	 * <p>Http server is started.
	 * @param args
	 *   <li>args[0]: Setting file of XML. (default "components.xml")
	 *   <li>args[1]: Name of HttpEngine. (default "server")
	 */
	public static void main(String[] args) {
		String config = args.length > 0 ? args[0] : XML;
		String serverKey = args.length > 1 ? args[1] : DEFAULT_SERVER_KEY;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		HttpEngine server = di.getBean(serverKey, HttpEngine.class);
		if (server == null) throw new IllegalArgumentException();
		Thread t = new Thread(server);
		t.start();
	}
}
