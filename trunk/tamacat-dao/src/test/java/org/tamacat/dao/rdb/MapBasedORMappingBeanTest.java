/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MapBasedORMappingBeanTest {

	MapBasedORMappingBean bean;
	User user;
	
	@Before
	public void setUp() throws Exception {
		bean = new MapBasedORMappingBean();
	}

	@After
	public void tearDown() throws Exception {
		bean.clear();
	}

	@Test
	public void testSetValueAndGetValue() {
		bean.setValue(User.USER_ID, "admin");
		assertEquals("admin", bean.getValue(User.USER_ID));
	}

	@Test
	public void testPutStringObject() {
		bean.put("users.user_id", "admin");
		assertEquals("admin", bean.getValue(User.USER_ID));
		assertEquals("admin", bean.get("users.user_id"));
	}

	@Test
	public void testMapping() {
		assertEquals("admin",
			bean.mapping(User.USER_ID, "admin").getValue(User.USER_ID));
	}

	@Test
	public void testIsUpdate() {
		bean.setValue(User.USER_ID, "admin");
		assertEquals(true, bean.isUpdate(User.USER_ID));
		assertEquals(false, bean.isUpdate(User.PASSWORD));
	}

	@Test
	public void testParse() {
		assertEquals("users.user_id", MapBasedORMappingBean.parse(User.USER_ID));
	}
}
