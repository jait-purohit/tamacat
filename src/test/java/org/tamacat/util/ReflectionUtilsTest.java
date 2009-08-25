/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReflectionUtilsTest {

	@Test
	public void testSetParameters() {
		Target instance = new Target();
		String methodName = "setName";
		String param = "scott";
		ReflectionUtils.setParameters(instance, methodName, param);
		assertEquals(param, instance.getName());
	}

	static class Target {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
