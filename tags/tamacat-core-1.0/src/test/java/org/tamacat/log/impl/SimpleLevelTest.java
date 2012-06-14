package org.tamacat.log.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleLevelTest {

	@Test
	public void testEqualsObject() {
		SimpleLevel info1 = SimpleLevel.INFO;
		SimpleLevel info2 = new SimpleLevel("INFO", 500);
		SimpleLevel info3 = new SimpleLevel("INFO", 999);

		assertTrue(info1.equals(info2));
		assertFalse(info1.equals(info3));
	}
	
	@Test
	public void testHashCode() {
		SimpleLevel info1 = SimpleLevel.INFO;
		SimpleLevel info2 = new SimpleLevel("INFO", 500);
		SimpleLevel info3 = new SimpleLevel("INFO", 999);
		
		assertTrue(info1.equals(info2));
		assertTrue(info1.hashCode() == info2.hashCode());
		
		assertFalse(info1.equals(info3));
		assertFalse(info1.hashCode() == info3.hashCode());
	}

	@Test
	public void testSimpleLevel() {
		SimpleLevel level = new SimpleLevel("TEST", 999);
		assertEquals("TEST", level.getLevel());
		assertEquals(999, level.getPriority());
	}

	@Test
	public void testGetLevel() {
		assertEquals("FATAL", SimpleLevel.FATAL.getLevel());
		assertEquals("ERROR", SimpleLevel.ERROR.getLevel());
		assertEquals("WARN", SimpleLevel.WARN.getLevel());
		assertEquals("INFO", SimpleLevel.INFO.getLevel());
		assertEquals("DEBUG", SimpleLevel.DEBUG.getLevel());
		assertEquals("TRACE", SimpleLevel.TRACE.getLevel());
	}

	@Test
	public void testToString() {
		assertEquals("FATAL", SimpleLevel.FATAL.toString());
		assertEquals("ERROR", SimpleLevel.ERROR.toString());
		assertEquals("WARN", SimpleLevel.WARN.toString());
		assertEquals("INFO", SimpleLevel.INFO.toString());
		assertEquals("DEBUG", SimpleLevel.DEBUG.toString());
		assertEquals("TRACE", SimpleLevel.TRACE.toString());
	}

	@Test
	public void testGetPriority() {
		assertEquals(900, SimpleLevel.FATAL.getPriority());
		assertEquals(800, SimpleLevel.ERROR.getPriority());
		assertEquals(700, SimpleLevel.WARN.getPriority());
		assertEquals(500, SimpleLevel.INFO.getPriority());
		assertEquals(400, SimpleLevel.DEBUG.getPriority());
		assertEquals(200, SimpleLevel.TRACE.getPriority());
	}
}
