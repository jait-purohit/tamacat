package org.tamacat.httpd.session;

public interface SessionSerializable {

	void updateSession();
	
	void setSessionStore(SessionStore sessionStore);
}
