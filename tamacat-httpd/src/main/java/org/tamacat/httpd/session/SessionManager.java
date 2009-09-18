/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.util.HashMap;

public final class SessionManager implements SessionListener {

	private
	  static final SessionManager SELF = new SessionManager();
	
	private
	  static final HashMap<String, Session> MANAGER
	  	= new HashMap<String, Session>();

	private static int defaultMaxInactiveInterval;

	static {
		defaultMaxInactiveInterval = 30 * 60 * 1000; //30min.
	}
	
	public
	  static SessionManager getInstance() {
		return SELF;
	}
	
	private
	  SessionManager() {}
	
	public Session getSession(String id) {
		return getSession(id, true);
	}
	
	public Session getSession(String id, boolean isCreate) {
		synchronized (MANAGER) {
			Session session = MANAGER.get(id);
			if (session != null) {
				if (System.currentTimeMillis() - session.getCreationDate().getTime()
					<= session.getMaxInactiveInterval()) {
					return session;
				} else {
					session.invalidate();
					return null;
				}
			} else if (isCreate) {
				return createSession();
			} else {
				return session;
			}
		}
	}
	
	@Override
	public int getMaxInactiveInterval() {
		return defaultMaxInactiveInterval;
	}
	
	public Session createSession() {
		Session session = new DefaultSession(SELF);
		MANAGER.put(session.getId(), session);
		return session;
	}

	@Override
	public void invalidate(Session session) {
		MANAGER.remove(session.getId());		
	}
}
