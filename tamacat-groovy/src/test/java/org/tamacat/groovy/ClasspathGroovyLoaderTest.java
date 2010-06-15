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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetGroovy() {
		Object o = ClasspathGroovyLoader.getInstance().getGroovy("/org/tamacat/groovy/test/Groovy_test");
		assertNotNull(o);
		assertEquals("org.tamacat.groovy.test.Groovy_test", o.getClass().getName());
	}
	
	@Test
	public void testRecompile() {
		Object o = ClasspathGroovyLoader.getInstance().getGroovy("/org/tamacat/groovy/test/Groovy_test");
		assertNotNull(o);
		assertEquals("org.tamacat.groovy.test.Groovy_test", o.getClass().getName());
		ClasspathGroovyLoader.getInstance().recompile();
	}
}
