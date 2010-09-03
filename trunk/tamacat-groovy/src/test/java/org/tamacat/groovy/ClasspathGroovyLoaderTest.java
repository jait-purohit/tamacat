/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClasspathGroovyLoaderTest {

	ClasspathGroovyLoader loader;
	
	@Before
	public void setUp() throws Exception {
		loader = new ClasspathGroovyLoader();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetGroovy() {
		Class<?> c = loader.loadClass("/org/tamacat/groovy/test/Groovy_test.groovy");
		assertNotNull(c);
		assertEquals("org.tamacat.groovy.test.Groovy_test", c.getName());
	}
	
	@Test
	public void testRecompile() {
		Class<?> c = loader.loadClass("/org/tamacat/groovy/test/Groovy_test.groovy");
		assertNotNull(c);
		assertEquals("org.tamacat.groovy.test.Groovy_test", c.getName());
		loader.recompile();
	}
}
