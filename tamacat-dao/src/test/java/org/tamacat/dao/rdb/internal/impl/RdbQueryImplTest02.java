/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.internal.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.rdb.FileData;

public class RdbQueryImplTest02 {

	RdbQueryImpl<FileData> query;
	
	@Before
	public void setUp() throws Exception {
		query = new RdbQueryImpl<FileData>();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetInsertSQL() {
		FileData data = new FileData();
		data.setValue(FileData.FILE_ID, "123");	
		
		query.addUpdateColumn(FileData.FILE_ID)
			.addUpdateColumn(FileData.DATA);
		
		assertEquals(
			"INSERT INTO file (file_id,data)"
			+ " VALUES ('123',?)", query.getInsertSQL(data));
	}
}
