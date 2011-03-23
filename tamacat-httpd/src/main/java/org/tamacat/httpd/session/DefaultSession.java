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

public class DefaultSession implements Session, Serializable {

	private static final long serialVersionUID = 1L;
	private Date creationDate;
	private Date lastAccessDate;
	private String id;
	private HashMap<String, Object> attributes;
	private int maxInactiveInterval; // = 30 * 60 * 1000; //30min.
	
	public DefaultSession() {
		this(30*60*1000);
	}
	
	public DefaultSession(int maxInactiveInterval) {
		this.creationDate = new Date();
		this.attributes = new HashMap<String, Object>();
		this.id = UniqueCodeGenerator.generate();
		this.maxInactiveInterval = maxInactiveInterval;
		updateSession();
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
	}

	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
		updateSession();
	}

	@Override
	public void setAttribute(String key, Object value) {
		if (value != null && !(value instanceof Serializable)) {
			System.err.println("Session#setAttribute value is not Serializable: "
				+ value.getClass().getName());
		}
		attributes.put(key, value);
		updateSession();
	}
	
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
	
	protected void updateSession() {
		lastAccessDate = new Date();
	}
}
