package org.tamacat.httpd.session;

import javax.management.MXBean;

@MXBean
public interface SessionMonitor {

	int getActiveSessions();
}
