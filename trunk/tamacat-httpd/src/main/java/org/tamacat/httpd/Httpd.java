/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;

public class Httpd {

	static final String XML = "components.xml";
	
	public static void main(String[] args) {
		DIContainer di = DI.configure(XML);
		if (di == null) throw new IllegalArgumentException(XML + " is not found.");
		HttpEngine server = di.getBean("server", HttpEngine.class);
		if (server == null) throw new IllegalArgumentException();
		server.start();
	}
}
