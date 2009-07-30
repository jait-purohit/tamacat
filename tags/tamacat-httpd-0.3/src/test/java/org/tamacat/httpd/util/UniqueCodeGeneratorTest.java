/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UniqueCodeGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerate() {
		for (int j=0; j<10; j++) {
			HashSet<String> uniq = new HashSet<String>(1000);
			for (int i=0; i<1000; i++) {
				String uuid = UniqueCodeGenerator.generate();
				if (uniq.contains(uuid) == false) {
					uniq.add(uuid);
					//System.out.println(uuid);
				} else {
					fail();
				}
			}
		}
	}

	@Test
	public void testGenerateString() {
	}
}
