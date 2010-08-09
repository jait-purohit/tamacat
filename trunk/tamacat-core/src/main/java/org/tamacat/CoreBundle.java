/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class CoreBundle implements BundleActivator, ServiceListener {
	
	@Override
	public void start(BundleContext context) throws Exception {
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
