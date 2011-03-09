package org.tamacat.httpd.session;

import java.io.IOException;

public interface SessionFactory extends SessionMonitor {

	Session getSession(String id);
	Session getSession(String id, boolean isCreate);
	
	Session createSession();
	void invalidate(Session session);
	
	void serialize() throws IOException;
	void deserialize();
	
	void release();
}
