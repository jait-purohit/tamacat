package org.tamacat.servlet.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.tamacat.servlet.HttpCoreServletSession;

public class HttpSessionManager {

	private ServletContext servletContext;
	
	private static final HashMap<String,HttpCoreServletSession> MANAGER
		= new HashMap<String, HttpCoreServletSession>();
	
	private static final LinkedHashMap<String, Long> LAST_ACCESS
		= new LinkedHashMap<String, Long>();
	
	private static final int CLEANUP_INTERVAL = 10000;
	
	HttpSessionManager(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public synchronized HttpCoreServletSession createSession() {
		HttpCoreServletSession session = new HttpSessionImpl(servletContext);
		MANAGER.put(session.getId(), session);
		LAST_ACCESS.put(session.getId(), session.getCreationTime());
		return session;
	}
	
	public synchronized HttpCoreServletSession getSession(String id, boolean isNew) {
		HttpCoreServletSession session = id != null ? MANAGER.get(id) : null;
		if (session == null && isNew) {
			session = createSession();
		} else if (session != null) {
			session.updateLastAccessedTime();
			LAST_ACCESS.put(session.getId(), session.getLastAccessedTime());
		}
		return session;
	}
	
	public static synchronized void invalidate(HttpSession session) {
		String id = session.getId();
		session.invalidate();
		LAST_ACCESS.remove(id);
		MANAGER.remove(id);
	}
	
	static class SessionCleanupThread extends Thread {
		public void run() {
			long now = System.currentTimeMillis();
			for (Entry<String, Long> entry : LAST_ACCESS.entrySet()) {
				String id = entry.getKey();
				HttpCoreServletSession session = MANAGER.get(id);
				int inactive = session.getMaxInactiveInterval();				
				long lastAccess = session.getLastAccessedTime();
				if (lastAccess <= 0) {
					lastAccess = entry.getValue();
				}
				if (now - lastAccess > inactive) {
					invalidate(session);
				}
			}
			try {
				wait(CLEANUP_INTERVAL);
			} catch (InterruptedException e) {
			}
		}
	}
}
