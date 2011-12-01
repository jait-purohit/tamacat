package org.tamacat.dao.meta;

import static org.junit.Assert.*;

import org.junit.Test;
import org.tamacat.dao.meta.RdbColumnDefine;

public class RdbColumnDefineTest {

	@Test
	public void testHashCode() {
		RdbColumnDefine pk = new RdbColumnDefine("primary key");
		
		assertEquals(-869111968, pk.hashCode());
		assertEquals(-869111968, RdbColumnDefine.PRIMARY_KEY.hashCode());
		assertEquals(31, new RdbColumnDefine(null).hashCode());
	}

	@Test
	public void testGetDefineName() {
		RdbColumnDefine pk = new RdbColumnDefine("primary key");
		assertEquals("primary key", pk.getDefineName());
	}

	@Test
	public void testEqualsObject() {
		RdbColumnDefine pk = new RdbColumnDefine("primary key");
		RdbColumnDefine nn = new RdbColumnDefine("not null");
		
		assertEquals(true, RdbColumnDefine.PRIMARY_KEY.equals(pk));
		assertEquals(true, pk.equals(pk));
		assertEquals(true, new RdbColumnDefine(null).equals(new RdbColumnDefine(null)));
		
		assertEquals(false, pk.equals(nn));
		assertEquals(false, pk.equals(null));
		assertEquals(false, pk.equals("primary key"));
		assertEquals(false, pk.equals(new RdbColumnDefine(null)));
		assertEquals(false, new RdbColumnDefine(null).equals(pk));
	}
}
