/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GroovyClassCacheTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetString() throws Exception {
		GroovyClassCache cache = new GroovyClassCache(10);
		cache.setCacheExpireTime(1000);
		cache.put("A", new GroovyFile(Target.class, new Date().getTime()));
		cache.put("B", new GroovyFile(Target.class, new Date().getTime()));
		cache.put("C", new GroovyFile(Target.class, new Date().getTime()));
		
		assertEquals(3, cache.size());
		assertNotNull(cache.get("A"));

		Thread.sleep(1500);
		
		assertNull(cache.get("A"));

		assertEquals(2, cache.size());
	}
}
