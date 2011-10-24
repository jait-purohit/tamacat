package org.tamacat.dao.rdb;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultRdbTableMetaDataTest {

	@Test
	public void testFind() {
		assertEquals(Dept.DEPT_ID, Dept.TABLE.find("dept_id"));
		assertEquals(Dept.DEPT_NAME, Dept.TABLE.find("dept_name"));
		assertEquals(Dept.DEPT_ID, Dept.TABLE.find("DEPT_ID"));
		assertEquals(Dept.DEPT_NAME, Dept.TABLE.find("DEPT_NAME"));
		assertEquals(null, Dept.TABLE.find("unknown"));
	}

}