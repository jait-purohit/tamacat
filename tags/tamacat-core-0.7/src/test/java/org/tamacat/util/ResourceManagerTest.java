/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ResourceManager;

public class ResourceManagerTest {

	Life1 target1 = new Life1();
	Life1 target2 = new Life1();
	
	@Before
	public void setUp() throws Exception {
		ResourceManager.getInstance().set(target1);
		System.out.printf("set: %s\n", target1);
		ResourceManager.getInstance().set(target2);
		System.out.printf("set: %s\n", target2);
	}

	//@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testRelease() {
		try {
			ResourceManager.getInstance().release();
		} catch (Exception e) {
			fail();
		}
	}

	static class Life1 {

		static final Log LOG = LogFactory.getLog(Life1.class);
		boolean isRunning;
		public boolean isRunning() {
			return isRunning;
		}

		public void start() {
			isRunning = true;
		}

		public void stop() {
			isRunning = false;
			System.out.printf("release: %s\n", this);
		}
	}
}
