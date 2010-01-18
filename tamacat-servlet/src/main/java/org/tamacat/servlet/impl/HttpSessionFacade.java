package org.tamacat.servlet.impl;

import java.util.HashMap;

import javax.servlet.ServletContext;

public class HttpSessionFacade {

	private static final HashMap<ServletContext, HttpSessionManager>
		MANAGERS = new HashMap<ServletContext, HttpSessionManager>();
	
	public static synchronized HttpSessionManager getInstance(ServletContext context) {
		HttpSessionManager manager = MANAGERS.get(context);
		if (manager == null) {
			manager = new HttpSessionManager(context);
			MANAGERS.put(context, manager);
		}
		return manager;
	}
}
