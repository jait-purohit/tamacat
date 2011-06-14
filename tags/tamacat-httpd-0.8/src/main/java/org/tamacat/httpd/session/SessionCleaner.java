package org.tamacat.httpd.session;

import java.util.Set;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class SessionCleaner implements Runnable {

	static final Log LOG = LogFactory.getLog(SessionCleaner.class);

	private int checkInterval =  30 * 1000; //default 30sec.
	private String name = "Cleaner";
	
	private SessionFactory manager;
	
	public void setSessionFactory(SessionFactory manager) {
		this.manager = manager;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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
						checkAndCleanup(id);
					}
				}
				Thread.sleep(checkInterval);
			}
		} catch (InterruptedException e) {
			LOG.debug(e.getMessage());
			LOG.warn("stop.");
		}
	}
	
	void checkAndCleanup(String id) {
		if (id != null) {
			try {
				Session session = manager.checkSession(id);
				if (session == null) LOG.debug("cleanup: " + id);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.warn(e.getMessage());
			}
		}
	}

}
