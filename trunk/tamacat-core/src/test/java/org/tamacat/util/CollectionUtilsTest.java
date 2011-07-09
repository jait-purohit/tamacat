package org.tamacat.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CollectionUtilsTest {

	@Test
	public void testConvertMapCollectionOfMapOfStringStringMapOfStringStringStringString() {
		Map<String, String> map1 = new LinkedHashMap<String, String>();
		map1.put("key1", "value1");
		map1.put("key2", "value2");
		map1.put("key3", "value3");
		Map<String, String> map2 = new LinkedHashMap<String, String>();
		map2.put("key1", "value1");
		map2.put("key2", "value2");
		map2.put("key3", "value3");
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		list.add(map1);
		list.add(map2);
		
		Map<String, String> remake = new LinkedHashMap<String, String>();

		Map<String, String> result = CollectionUtils.convertMap(list, remake, "key2", "value2");
		assertTrue(result instanceof LinkedHashMap);
		assertEquals(1, result.size());
		assertSame(result, remake);
	}

	@Test
	public void testConvertMapMapOfKVCollectionOfKCollectionOfV() {
		List<String> keys = new ArrayList<String>();
		keys.add("key1");
		keys.add("key2");
		keys.add("key3");
		
		List<String> values = new ArrayList<String>();
		values.add("values1");
		values.add("values2");
		values.add("values3");
		
		Map<String, String> map = new LinkedHashMap<String, String>(); 
		Map<String, String> result = CollectionUtils.convertMap(map, keys, values);
		assertTrue(result instanceof LinkedHashMap);
		assertEquals(3, result.size());
		assertSame(result, map);
	}
	
	@Test
	public void testConvertMapError() {
		List<String> keys = new ArrayList<String>();
		keys.add("key1");
		keys.add("key2");

		List<String> values = new ArrayList<String>();
		values.add("values1");
		
		try {
			CollectionUtils.convertMap(new LinkedHashMap<String, String>(), keys, values);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
		
		assertNotNull(new CollectionUtils());
	}

}
