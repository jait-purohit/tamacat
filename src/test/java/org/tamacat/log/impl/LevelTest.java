package org.tamacat.log.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class LevelTest {

	@Test
	public void testHashCode() {
		Level<String> level = new Level<String>("1");
		assertEquals(80, level.hashCode());
	}

	@Test
	public void testLevel() {
		Level<String> level = new Level<String>("1");
		assertNotNull(level.getLevel());
		
		Level<String> level2 = new Level<String>("");
		assertNotNull(level2.getLevel());
		
		Level<String> level3 = new Level<String>(null);
		assertNull(level3.getLevel());
	}

	@Test
	public void testGetLevel() {
		Level<String> level = new Level<String>("1");
		assertEquals("1", level.getLevel());
	}

	@Test
	public void testEqualsObject() {
		Level<String> level = new Level<String>("1");
		Level<String> level1 = new Level<String>("1");
		Level<String> level2 = new Level<String>("2");

		assertTrue(level.equals(level1));
		assertFalse(level1.equals(level2));
	}

}
