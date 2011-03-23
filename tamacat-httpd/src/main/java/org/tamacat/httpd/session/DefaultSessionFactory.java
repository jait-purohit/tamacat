/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class DefaultSessionFactory implements SessionFactory {
	static final Log LOG = LogFactory.getLog(DefaultSessionFactory.class);

	private List<SessionListener> listeners = new ArrayList<SessionListener>();
	
	private int defaultMaxInactiveInterval = 30 * 60 * 1000; //30min.
	
	private SessionStore sessionStore = new MemorySessionStore();
	
	public void setSessionListener(SessionListener listener) {
		listeners.add(listener);
	}
	
	public void setDefaultMaxInactiveInterval(int max) {
		defaultMaxInactiveInterval = max;
	}
	
	public
	  DefaultSessionFactory() {
		register();
	}
	
	public Set<String> getActiveSessionIds() {
		return sessionStore.getActiveSessionIds();
	}
	
	public Session getSession(String id) {
		return getSession(id, true);
	}
	
	public Session getSession(String id, boolean isCreate) {
		Session session = sessionStore.load(id);
		if (session != null) {
			if (System.currentTimeMillis() - session.getLastAccessDate().getTime()
				<= session.getMaxInactiveInterval()) {
				session.setLastAccessDate(new Date());
				return session;
			} else {
				invalidate(session);
				return null;
			}
		} else if (isCreate) {
			return createSession();
		} else {
			return session;
		}
	}
	
	public int getMaxInactiveInterval() {
		return defaultMaxInactiveInterval;
	}
	
	@Override
	public int getActiveSessions() {
		return sessionStore.getActiveSessions();
	}
	
	public Session createSession() {
		Session session = new DefaultSession(getMaxInactiveInterval());
		sessionStore.store(session);
		for (SessionListener listener : listeners) {
			listener.sessionCreated(session);
		}
		return session;
	}

	public void invalidate(Session session) {
		String id = session.getId();
		try {
			for (SessionListener listener : listeners) {
				listener.sessionDestroyed(session);
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
		try {
			session.invalidate();
		} finally {
			sessionStore.delete(id);
		}
	}
	
	@Override
	public synchronized void release() {
		sessionStore.release();
	}
	
	@Override
	public void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}
	
	void register() {
		try {
			String name = "org.tamacat.httpd:type=DefaultSessionFactory";
			ObjectName oname = new ObjectName(name);			
			MBeanServer server = ManagementFactory.getPlatformMBeanServer(); 
        	server.registerMBean(this, oname);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}
}
