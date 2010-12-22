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
	private Date lastAccessDate;
	private String id;
	private HashMap<String, Object> attributes;
	private int maxInactiveInterval; // = 30 * 60 * 1000; //30min.
	private transient SessionListener listener;
	
	public DefaultSession(SessionListener listener) {
		this.creationDate = new Date();
		this.lastAccessDate = new Date();
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
	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	
	@Override
	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
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
		lastAccessDate = new Date();
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
		lastAccessDate = new Date();
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
