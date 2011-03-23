package org.tamacat.httpd.session;

public interface SessionFactory extends SessionMonitor {

	Session getSession(String id);
	Session getSession(String id, boolean isCreate);
	
	Session createSession();
	void invalidate(Session session);
	
	//void serialize() throws IOException;
	//void deserialize(Session session);
	
	void release();
	
	void setSessionStore(SessionStore sessionStore);
}
