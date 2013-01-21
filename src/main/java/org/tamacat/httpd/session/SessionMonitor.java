package org.tamacat.httpd.session;

import java.util.Set;

import javax.management.MXBean;

@MXBean
public interface SessionMonitor {

	int getActiveSessions();
	
	Set<String> getActiveSessionIds();
}
