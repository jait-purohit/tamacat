/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public final class DefaultSessionFactory implements SessionFactory {
	static final Log LOG = LogFactory.getLog(DefaultSessionFactory.class);
	
	private static final ConcurrentHashMap<String, Session> 
		MANAGER = new ConcurrentHashMap<String, Session>();

	private List<SessionListener> listeners = new ArrayList<SessionListener>();
	
	private int defaultMaxInactiveInterval = 30 * 60 * 1000; //30min.
	
	private SessionSerializer serializer;// = new FileSessionSerializer();
	
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
		return MANAGER.keySet();
	}
	
	public Session getSession(String id) {
		return getSession(id, true);
	}
	
	public Session getSession(String id, boolean isCreate) {
		synchronized (MANAGER) {
			Session session = MANAGER.get(id);
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
	}
	
	public int getMaxInactiveInterval() {
		return defaultMaxInactiveInterval;
	}
	
	@Override
	public int getActiveSessions() {
		return MANAGER.size();
	}
	
	public Session createSession() {
		Session session = new DefaultSession(getMaxInactiveInterval());
		MANAGER.put(session.getId(), session);
		for (SessionListener listener : listeners) {
			listener.sessionCreated(session);
		}
		return session;
	}

	public void invalidate(Session session) {
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
			MANAGER.remove(session.getId());
		}
	}
	
	public void serialize() throws IOException {
		serializer.serialize(this);
	}
	
	public void deserialize() {
		serializer.deserialize(this);
	}
	
	public synchronized void release() {
		MANAGER.clear();
	}
	
	public void setSessionSerializer(SessionSerializer serializer) {
		this.serializer = serializer;
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
