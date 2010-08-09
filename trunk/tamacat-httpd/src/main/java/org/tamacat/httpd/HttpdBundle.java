/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;

public class HttpdBundle implements BundleActivator, ServiceListener {
	
	public static final String XML = "components.xml";
	private static final String DEFAULT_SERVER_KEY = "server";
	
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println(getClass().getClassLoader());
		String config = XML;
		String serverKey = DEFAULT_SERVER_KEY;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		HttpEngine server = di.getBean(serverKey, HttpEngine.class);
		if (server == null) throw new IllegalArgumentException();

		context.addServiceListener(this);
		System.out.println("start " + context.getBundle());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		context.removeServiceListener(this);
		System.out.println("stop " + context.getBundle());
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		System.out.println(event.getServiceReference());
	}
}
