/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

public interface RdbDaoExecuteHandler {

	void handleBeforeExecuteQuery(RdbDaoEvent event);
	void handleAfterExecuteQuery(RdbDaoEvent event);
	
	void handleBeforeExecuteUpdate(RdbDaoEvent event);
	int handleAfterExecuteUpdate(RdbDaoEvent event);
}
