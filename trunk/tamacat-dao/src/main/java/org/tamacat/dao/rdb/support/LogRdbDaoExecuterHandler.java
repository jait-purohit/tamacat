/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.support;

import org.tamacat.dao.rdb.RdbDaoEvent;
import org.tamacat.dao.rdb.RdbDaoExecuteHandler;
import org.tamacat.log.LogFactory;

public class LogRdbDaoExecuterHandler implements RdbDaoExecuteHandler {

	public LogRdbDaoExecuterHandler() {}
	
	@Override
	public void handleAfterExecuteQuery(RdbDaoEvent event) {
		//none.
	}

	@Override
	public int handleAfterExecuteUpdate(RdbDaoEvent event) {
		return event.getResult();
	}

	@Override
	public void handleBeforeExecuteQuery(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug(event.getQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleBeforeExecuteUpdate(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug(event.getQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
