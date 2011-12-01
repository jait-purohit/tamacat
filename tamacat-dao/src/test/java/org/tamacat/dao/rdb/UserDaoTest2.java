/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tamacat.dao.Condition;
import org.tamacat.dao.RdbDaoFactory;
import org.tamacat.dao.RdbSearch;
import org.tamacat.dao.RdbSort;

public class UserDaoTest2 {

    UserDao dao;

    @BeforeClass
    public static void createTable() {
    	UserDao dao = RdbDaoFactory.getRdbDao(UserDao.class);
    	try {
        	dao.setDatabase("javadb");
    		dao.createTable();
    	} catch (Exception e){
    		e.printStackTrace();
    	} finally {
    		if (dao != null) dao.release();
    	}
    }
    
    @AfterClass
    public static void dropTable() {
    	UserDao dao = RdbDaoFactory.getRdbDao(UserDao.class);
    	try {
        	dao.setDatabase("javadb");
    		dao.dropTable();
    	} catch (Exception e){
    		e.printStackTrace();
    	} finally {
    		if (dao != null) dao.release();
    	}
    }
    
    @Before
    public void setUp() throws Exception {
        dao = new UserDao();
        dao.setDatabase("javadb");
    }

    @After
    public void tearDown() throws Exception {
        if (dao != null) dao.release();
    }
    
    @Test
    public void testCreate() {
        User user = new User();
        user.setValue(User.USER_ID, "admin").setValue(User.PASSWORD, "password");
        int result = dao.create(user);
        assertEquals(1, result);
    }

    @Test
    public void testSearchUser() {
        User user = new User();
        user.setValue(User.USER_ID, "admin");
        user = dao.search(user);
        assertNotNull(user);
    }

    @Test
    public void testSearchListSearchSort() {
        RdbSearch search = dao.createRdbSearch().and(User.USER_ID, Condition.LIKE_PART, "admin");
        search.setMax(10);
        RdbSort sort = dao.createRdbSort();
        Collection<User> list = dao.searchList(search, sort);
        for (User u : list) {
            assertNotNull(u);
            //System.out.printf("user_id=%s, password=%s\n",
            //	u.getValue(User.USER_ID), u.getValue(User.PASSWORD));
        }
        assertNotNull(list);
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setValue(User.USER_ID, "admin").setValue(User.PASSWORD, "password");
        int result = dao.update(user);
        assertEquals(1, result);
    }

    @Test
    public void testDelete() {
        User user = new User();
        user.setValue(User.USER_ID, "admin");
        int result = dao.delete(user);
        assertEquals(1, result);
    }
}