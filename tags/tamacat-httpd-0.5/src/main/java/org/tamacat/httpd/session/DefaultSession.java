/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.tamacat.util.UniqueCodeGenerator;

class DefaultSession implements Session, Serializable {

	private static final long serialVersionUID = 1L;
	private Date creationDate;
	private String id;
	private HashMap<String, Object> attributes;
	private int maxInactiveInterval; // = 30 * 60 * 1000; //30min.
	private SessionListener listener;
	
	public DefaultSession(SessionListener listener) {
		this.creationDate = new Date();
		this.attributes = new HashMap<String, Object>();
		this.id = UniqueCodeGenerator.generate();
		this.listener = listener;
		this.maxInactiveInterval = listener.getMaxInactiveInterval();
	}
	
	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public Set<String> getAttributeKeys() {
		return attributes.keySet();
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void invalidate() {
		attributes.clear();
		listener.invalidate(this);
	}

	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
}