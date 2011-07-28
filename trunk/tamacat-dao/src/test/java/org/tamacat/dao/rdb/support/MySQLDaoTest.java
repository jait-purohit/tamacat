package org.tamacat.dao.rdb.support;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.rdb.User;
import org.tamacat.dao.rdb.internal.RdbQuery;

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
		dao.createRdbSearch();
		
	}

	@Test
	public void testSearchListRdbQueryOfTIntInt() {
		RdbQuery<User> query = dao.createQuery();
		//dao.searchList(query, 1, 5);
		assertNotNull(query);
	}
}
