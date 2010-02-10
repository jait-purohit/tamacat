/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.util.HashMap;
import java.util.Set;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

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
	
	public
	  static void setDefaultMaxInactiveInterval(int max) {
		defaultMaxInactiveInterval = max;
	}
	
	private
	  SessionManager() {
		new Thread(new SessionCleaner(), "Cleaner").start();
	}
	
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
	
	static class SessionCleaner implements Runnable {
		static final Log LOG = LogFactory.getLog(SessionCleaner.class);
		private int checkInterval =  60 * 1000; //60sec.
		
		void setCheckInterval(int checkInterval) {
			this.checkInterval = checkInterval;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					LOG.trace("clean check.");
					Set<String> ids = MANAGER.keySet();
					for (String id : ids) {
						checkAndCleanup(MANAGER.get(id));
					}
					Thread.sleep(checkInterval);
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
			}
		}
		
		void checkAndCleanup(Session session) {
			if (session != null) {
				if (LOG.isTraceEnabled()) {
					LOG.info(System.currentTimeMillis()
						- session.getCreationDate().getTime()
						+ " > " + session.getMaxInactiveInterval());
				}
				if (System.currentTimeMillis() - session.getCreationDate().getTime()
					> session.getMaxInactiveInterval()) {
					try {
						String id = session.getId();
						session.invalidate();
						LOG.debug("cleanup: " + id);
					} catch (Exception e) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}
	}
}
