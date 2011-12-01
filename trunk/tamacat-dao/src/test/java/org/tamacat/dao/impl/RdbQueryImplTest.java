/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.Condition;
import org.tamacat.dao.SQLParser;
import org.tamacat.dao.impl.RdbQueryImpl;
import org.tamacat.dao.rdb.Dept;
import org.tamacat.dao.rdb.User;

public class RdbQueryImplTest {

	RdbQueryImpl<User> query;
	
	@Before
	public void setUp() throws Exception {
		query = new RdbQueryImpl<User>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSelectColumns_addSelectColumn() {
		query.addSelectColumn(User.USER_ID);
		assertEquals(1, query.getSelectColumns().size());
	}
	
	@Test
	public void testGetSelectColumns_addSelectColumns() {
		query.addSelectColumns(User.TABLE.getColumns());
		assertEquals(3, query.getSelectColumns().size());
	}
	
	@Test
	public void testGetUpdateColumns_addUpdateColumn() {
		query.addUpdateColumn(User.PASSWORD);
		assertEquals(1, query.getUpdateColumns().size());
	}

	@Test
	public void testGetUpdateColumns_addUpdateColumns() {
		query.addUpdateColumns(User.TABLE.getColumns());
		assertEquals(3, query.getUpdateColumns().size());
	}
	
	@Test
	public void testGetSelectSQL_AddSelectColumn() {
		query.addSelectColumn(User.USER_ID);
		query.addSelectColumn(User.PASSWORD);
		query.addSelectColumn(User.DEPT_ID);
		assertEquals(
			"SELECT users.user_id,users.password,users.dept_id FROM users",
			query.getSelectSQL()
		);
	}

	@Test
	public void testGetSelectSQL_AddSelectColumns() {
		query.addSelectColumns(User.TABLE.getColumns());
		assertEquals(
			"SELECT users.user_id,users.password,users.dept_id FROM users",
			query.getSelectSQL()
		);
	}
	
	@Test
	public void testGetInsertSQL() {
		User user = new User();
		user.setValue(User.USER_ID, "admin");
		user.setValue(User.PASSWORD, "test");
		user.setValue(User.DEPT_ID, "123");
		query.addUpdateColumns(User.TABLE.getColumns());
		
		assertEquals(
			"INSERT INTO users (user_id,password,dept_id)"
			+ " VALUES ('admin','test','123')", query.getInsertSQL(user));
	}

	@Test
	public void testGetUpdateSQL() {
		User user = new User();
		user.setValue(User.USER_ID, "admin");
		user.setValue(User.PASSWORD, "test");
		query.addUpdateColumn(User.USER_ID);
		query.addUpdateColumn(User.PASSWORD);
		query.addWhere("and", 
			new SQLParser().value(User.USER_ID, Condition.EQUAL, "admin"));
		assertEquals(
			"UPDATE users SET password='test' WHERE users.user_id='admin'",
			query.getUpdateSQL(user));
	}

	@Test
	public void testGetDeleteSQL() {
		User user = new User();
		user.setValue(User.USER_ID, "admin");
		query.addUpdateColumn(User.USER_ID);
		assertEquals(
			"DELETE FROM users WHERE users.user_id='admin'",
			query.getDeleteSQL(user));
	}

	@Test
	public void testAddConnectTable() {
		query.addConnectTable(User.DEPT_ID, Dept.DEPT_ID);
		assertEquals(" WHERE users.dept_id=dept.dept_id", query.where.toString());
	}

	@Test
	public void testGetTimestampString() {
		assertEquals("current_timestamp", query.getTimestampString());
	}

	@Test
	public void testGetColumnName() {
		assertEquals("users.user_id", RdbQueryImpl.getColumnName(User.USER_ID));
	}
}
