package org.tamacat.servlet;

import javax.servlet.http.HttpSession;

public interface HttpCoreServletSession extends HttpSession {

	void updateLastAccessedTime();
}
