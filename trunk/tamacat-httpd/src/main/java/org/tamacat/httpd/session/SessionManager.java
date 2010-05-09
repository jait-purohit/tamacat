/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public final class SessionManager implements SessionListener {
	static final Log LOG = LogFactory.getLog(SessionManager.class);

	private
	  static final SessionManager SELF = new SessionManager();
	
	private
	  static ConcurrentHashMap<String, Session> MANAGER
	  	= new ConcurrentHashMap<String, Session>();

	private static int defaultMaxInactiveInterval;

	private static Thread CLEANER;
	
	private SessionSerializer serializer = new FileSessionSerializer();
	
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
		//start session cleaning thread.
		CLEANER = new Thread(new SessionCleaner(), "Cleaner");
		CLEANER.start();
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
	
	public int getCountSessions() {
		return MANAGER.size();
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
	
	public void serialize() throws IOException {
		serializer.serialize();
	}
	
	
	public void deserialize() throws IOException {
		serializer.deserialize();
	}
	
	public synchronized void release() {
		CLEANER.interrupt();
		MANAGER.clear();
	}
	
	public void setSessionSerializer(SessionSerializer serializer) {
		this.serializer = serializer;
	}
	
	public interface SessionSerializer {
		void serialize() throws IOException;
		
		void deserialize() throws IOException;
	}
	
	class FileSessionSerializer implements SessionSerializer {
		private static final String DEFAULT_FILE_NAME = "session.ser";
		private String fileName = DEFAULT_FILE_NAME;
		
		void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		public void serialize() throws IOException {
			synchronized (MANAGER) {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(fileName));
				out.writeObject(MANAGER);
				out.close();
			}
		}
		
		@Override
		public void deserialize() throws IOException {
			synchronized (MANAGER) {
				ObjectInputStream in = new ObjectInputStream(
						new FileInputStream(fileName));
				try {
					@SuppressWarnings("unchecked")
					ConcurrentHashMap<String, Session> manager
						= (ConcurrentHashMap) in.readObject();
					if (manager != null) {
						MANAGER = manager;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				in.close();
			}
		}
	}
	
	/**
	 * <p>The internal class of session invalidate.
	 */
	static class SessionCleaner implements Runnable {
		private int checkInterval =  30 * 1000; //default 30sec.
		
		void setCheckInterval(int checkInterval) {
			this.checkInterval = checkInterval;
		}
		
		@Override
		public void run() {
			try {
				while(true) {
					LOG.trace("clean check.");
					Set<String> ids = MANAGER.keySet();
					for (String id : ids) {
						checkAndCleanup(MANAGER.get(id));
					}
					Thread.sleep(checkInterval);
				}
			} catch (InterruptedException e) {
				LOG.debug(e.getMessage());
				LOG.warn("stop.");
			}
		}
		
		void checkAndCleanup(Session session) {
			if (session != null) {
				String id = session.getId();
				if (LOG.isTraceEnabled()) {
					LOG.info(System.currentTimeMillis()
						- session.getCreationDate().getTime()
						+ " > " + session.getMaxInactiveInterval());
				}
				if (System.currentTimeMillis() - session.getCreationDate().getTime()
					> session.getMaxInactiveInterval()) {
					try {
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
