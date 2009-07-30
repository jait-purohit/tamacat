/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CacheLRU<K,V> {

	private int maxSize;
	private LinkedHashMap<K, V> cache;
	private ArrayList<K> used;
	
	public CacheLRU(int maxSize) {
		this.maxSize = maxSize;
		this.cache = new LinkedHashMap<K,V>(maxSize);
		this.used = new ArrayList<K>(maxSize);
	}
	
	public CacheLRU() {
		this(10);
	}
	
	public synchronized V get(K key) {
		updateUsed(key);
		return cache.get(key);
	}
	
	public synchronized V put(K key, V value) {
		if (cache.size() >= maxSize && used.size() > 0) {
			cache.remove(used.get(0));
			used.remove(0);
		}
		updateUsed(key);
		return cache.put(key, value);
	}
	
	private void updateUsed(K key) {
		used.remove(key);
		used.add(key);		
	}
	
	public synchronized int size() {
		return cache.size();
	}
	
	public synchronized V remove(K key) {
		used.remove(key);
		return cache.remove(key);
	}
	
	public synchronized void clear() {
		cache.clear();
		used.clear();
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "@" + hashCode() + "" + cache.toString();
	}
}
