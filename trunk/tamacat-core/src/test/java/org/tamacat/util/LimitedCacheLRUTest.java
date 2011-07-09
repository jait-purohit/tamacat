package org.tamacat.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LimitedCacheLRUTest {

	LimitedCacheLRU<String, CacheImpl> cache;
	
	static class CacheImpl implements LimitedCacheObject {
		@Override
		public boolean isCacheExpired(long expire) {
			return false;
		}
	}
	
	@Before
	public void setUp() throws Exception {
		cache = new LimitedCacheLRU<String, CacheImpl>(5,10);
	}

	@After
	public void tearDown() throws Exception {
		cache.clear();
	}

	@Test
	public void testGet() {
		assertNull(cache.get("key1"));
	}

	@Test
	public void testLimitedCacheLRU() {
		cache = new LimitedCacheLRU<String, CacheImpl>(5,10);
	}

}
