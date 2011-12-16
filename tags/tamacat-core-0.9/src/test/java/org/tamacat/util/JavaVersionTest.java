package org.tamacat.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class JavaVersionTest {

	@Test
	public void testMain() {
		assertTrue(1.6f <= JavaVersion.JAVA_VERSION);
	}
}
