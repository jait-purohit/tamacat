/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.action;

import org.tamacat.httpd.core.RequestContext;

public class DefaultAction {

	public void top(RequestContext request) {
		System.out.println("top() id=" + request.getParameter("id", 123));
	}
}
