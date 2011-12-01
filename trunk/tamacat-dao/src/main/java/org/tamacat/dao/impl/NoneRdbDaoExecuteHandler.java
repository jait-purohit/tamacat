/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import org.tamacat.dao.event.RdbDaoEvent;
import org.tamacat.dao.event.RdbDaoExecuteHandler;

public class NoneRdbDaoExecuteHandler implements RdbDaoExecuteHandler {

	@Override
	public void handleAfterExecuteQuery(RdbDaoEvent event) {
	}

	@Override
	public int handleAfterExecuteUpdate(RdbDaoEvent event) {
		if (event == null) return 0;
		else return event.getResult();
	}

	@Override
	public void handleBeforeExecuteQuery(RdbDaoEvent event) {
	}

	@Override
	public void handleBeforeExecuteUpdate(RdbDaoEvent event) {
	}
}
