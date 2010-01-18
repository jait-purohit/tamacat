package org.tamacat.servlet.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.tamacat.servlet.HttpCoreServletSession;
import org.tamacat.util.UniqueCodeGenerator;

public class HttpSessionImpl implements HttpCoreServletSession {

	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	private boolean isNew;
	private int maxInactiveInterval;
	private ServletContext servletContext;
	private long creationTime;
	private long lastAccessedTime;
	private String id;
	
	public HttpSessionImpl(ServletContext servletContext) {
		this.servletContext = servletContext;
		this.id = UniqueCodeGenerator.generate();
		this.creationTime = System.currentTimeMillis();
	}
	
	public void updateLastAccessedTime() {
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		return Collections.enumeration(attributes.entrySet());
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return servletContext;
	}

	@Override
	@SuppressWarnings("deprecation")
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String key) {
		return getAttribute(key);
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override
	public void putValue(String key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public void removeAttribute(String key) {
		this.attributes.remove(key);
	}

	@Override
	public void removeValue(String key) {
		attributes.remove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
}
