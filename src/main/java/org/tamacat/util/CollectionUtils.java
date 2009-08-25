/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.util.Collection;

import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {

	public static Map<String, String> convertMap(
			final Collection<Map<String,String>> list, final Map<String,String> remake,
			final String key, final String value) {
		for (Map<String,String> map : list) {
			remake.put(map.get(key), map.get(value));
		}
		return remake;
	}
	
	public static <K,V> Map<K, V> convertMap(
			final Map<K,V> map, final Collection<K> keys, final Collection<V> values) {
		if (keys.size() != values.size()) {
			throw new IllegalArgumentException("Invalid Collection. Collection size is not equals.");
		}
		Iterator<V> valueIt = values.iterator();
		for (K key : keys) {
			map.put(key, valueIt.next());
		}
		return map;
	}
}
