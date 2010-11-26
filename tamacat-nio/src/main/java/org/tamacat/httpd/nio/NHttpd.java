/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;

/**
 * <p>It is the start class of the http server.
 * The component setting in {@code nio-httpd.xml}.
 */
public class NHttpd {

	public static final String XML = "nio-httpd.xml";
	
	public static void main(String[] args) {
		String config = args.length > 0 ? args[0] : XML;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		NHttpEngine server = di.getBean("server", NHttpEngine.class);
		if (server == null) throw new IllegalArgumentException();
		server.startHttpd();
	}
}
