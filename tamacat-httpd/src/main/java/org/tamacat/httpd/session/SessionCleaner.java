package org.tamacat.httpd.session;

import java.util.Set;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class SessionCleaner implements Runnable {

	static final Log LOG = LogFactory.getLog(SessionCleaner.class);

	private int checkInterval =  30 * 1000; //default 30sec.
	
	private SessionFactory manager;
	
	public SessionCleaner(SessionFactory manager) {
		this.manager = manager;
	}
	
	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				LOG.debug("clean check.");
				Set<String> ids = manager.getActiveSessionIds();
				if (ids != null) {
					for (String id : ids) {
						checkAndCleanup(manager.getSession(id, false));
					}
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
				LOG.trace(System.currentTimeMillis()
					- session.getLastAccessDate().getTime()
					+ " > " + session.getMaxInactiveInterval());
			}
			if (System.currentTimeMillis() - session.getLastAccessDate().getTime()
				> session.getMaxInactiveInterval()) {
				try {
					session.invalidate();
					manager.invalidate(session);
					LOG.debug("cleanup: " + id);
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
			}
		}
	}

}
