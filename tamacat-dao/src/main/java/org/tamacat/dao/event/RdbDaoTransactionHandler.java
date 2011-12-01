/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.event;


public interface RdbDaoTransactionHandler {

	void handleTransantionStart(RdbDaoEvent event);
	void handleTransantionEnd(RdbDaoEvent event);
	
	void handleBeforeCommit(RdbDaoEvent event);
	void handleAfterCommit(RdbDaoEvent event);
	
	void handleBeforeRollback(RdbDaoEvent event);
	void handleAfterRollback(RdbDaoEvent event);
	
	void handleException(RdbDaoEvent event, Throwable cause);
	
	void handleRelease(RdbDaoEvent event);
}
