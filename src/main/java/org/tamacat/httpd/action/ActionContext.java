/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActionContext {

	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	
	public List<String> getAttributeNames() {
		return new ArrayList<String>(attributes.keySet());
	}
	
	public Object getAttribute(String id) {
		return attributes.get(id);
	}

	public Object removeAttribute(String id) {
		return attributes.remove(id);
	}

	public void setAttribute(String id, Object obj) {
		attributes.put(id, obj);
	}
}
