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
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.IOUtils;

public final class SessionManager implements SessionListener, SessionMonitor {
	static final Log LOG = LogFactory.getLog(SessionManager.class);

	private
	  static final SessionManager SELF = new SessionManager();
	
	private static final ConcurrentHashMap<String, Session> 
		MANAGER = new ConcurrentHashMap<String, Session>();

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
		register();
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
	
	@Override
	public int getActiveSessions() {
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
	
	public void deserialize() {
		serializer.deserialize();
	}
	
	public synchronized void release() {
		CLEANER.interrupt();
		MANAGER.clear();
	}
	
	public void setSessionSerializer(SessionSerializer serializer) {
		this.serializer = serializer;
	}
	
	void register() {
		try {
			String name = "org.tamacat.httpd:type=SessionManager";
			ObjectName oname = new ObjectName(name);			
			MBeanServer server = ManagementFactory.getPlatformMBeanServer(); 
        	server.registerMBean(this, oname);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}
	
	public interface SessionSerializer {
		void serialize();
		
		void deserialize();
	}
	
	class FileSessionSerializer implements SessionSerializer {
		private static final String DEFAULT_FILE_NAME = "session.ser";
		private String fileName = DEFAULT_FILE_NAME;
		
		void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		public void serialize() {
			synchronized (MANAGER) {
				ObjectOutputStream out = null;
				try {
					out = new ObjectOutputStream(
						new FileOutputStream(fileName));
					out.writeObject(MANAGER);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					IOUtils.close(out);
				}
			}
		}
		
		@Override
		public void deserialize() {
			synchronized (MANAGER) {
				ObjectInputStream in = null;
				try {
					in = new ObjectInputStream(
							new FileInputStream(fileName));
					@SuppressWarnings({ "unchecked", "rawtypes" })
					ConcurrentHashMap<String, Session> manager
						= (ConcurrentHashMap) in.readObject();
					if (manager != null) {
						MANAGER.putAll(manager);
					}
				} catch (IOException e) {
					LOG.warn(e.getMessage());
				} catch (ClassNotFoundException e) {
					LOG.warn(e.getMessage());
				} finally {
					IOUtils.close(in);
				}
			}
		}
	}
	
	/**
	 * <p>The internal class of session invalidate.
	 */
	class SessionCleaner implements Runnable {
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
						- session.getLastAccessDate().getTime()
						+ " > " + session.getMaxInactiveInterval());
				}
				if (System.currentTimeMillis() - session.getLastAccessDate().getTime()
					> session.getMaxInactiveInterval()) {
					try {
						session.invalidate();
						MANAGER.remove(id);
						LOG.debug("cleanup: " + id);
					} catch (Exception e) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}
	}
}
