/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.support;

import org.tamacat.dao.rdb.RdbDaoEvent;
import org.tamacat.dao.rdb.RdbDaoExecuteHandler;

public class NoneRdbDaoExecuteHandler implements RdbDaoExecuteHandler {

	@Override
	public void handleAfterExecuteQuery(RdbDaoEvent event) {
	}

	@Override
	public int handleAfterExecuteUpdate(RdbDaoEvent event) {
		return event.getResult();
	}

	@Override
	public void handleBeforeExecuteQuery(RdbDaoEvent event) {
	}

	@Override
	public void handleBeforeExecuteUpdate(RdbDaoEvent event) {
	}
}
