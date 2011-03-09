package org.tamacat.httpd.session;

public interface SessionSerializer {
	
	void serialize(SessionFactory factory);
	
	void deserialize(SessionFactory factory);
}
