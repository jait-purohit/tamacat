package org.tamacat.httpd.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class DefaultSessionAttributes extends HashMap<String, Object> implements SessionAttributes {

	private static final long serialVersionUID = -8046047687990935361L;

	@Override
	public Object getAttribute(String key) {
		return super.get(key);
	}
	
	@Override
	public Set<String> getAttributeKeys() {
		return super.keySet();
	}

	@Override
	public void removeAttribute(String key) {
		super.remove(key);
	}
	
	@Override
	public void setAttribute(String key, Object value) {
		if (value != null && !(value instanceof Serializable)) {
			System.err.println("SessionAttributes#setAttribute value is not Serializable: "
				+ value.getClass().getName());
		}
		super.put(key, value);
	}
}
