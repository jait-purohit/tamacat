package org.tamacat.dao.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.Query;
import org.tamacat.dao.test.User;

public class MySQLDaoTest {

	MySQLDao<User> dao;
	
	@Before
	public void setUp() throws Exception {
		dao = new MySQLDao<User>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateRdbSearch() {
		dao.createSearch();
		
	}

	@Test
	public void testSearchListRdbQueryOfTIntInt() {
		Query<User> query = dao.createQuery();
		//dao.searchList(query, 1, 5);
		assertNotNull(query);
	}
}
