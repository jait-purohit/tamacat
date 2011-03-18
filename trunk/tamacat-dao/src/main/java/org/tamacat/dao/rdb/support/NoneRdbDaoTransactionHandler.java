/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb.support;

import org.tamacat.dao.rdb.RdbDaoEvent;
import org.tamacat.dao.rdb.RdbDaoTransactionHandler;

public class NoneRdbDaoTransactionHandler implements RdbDaoTransactionHandler {

	@Override
	public void handleAfterCommit(RdbDaoEvent event) {
	}

	@Override
	public void handleAfterRollback(RdbDaoEvent event) {
	}

	@Override
	public void handleBeforeCommit(RdbDaoEvent event) {
	}

	@Override
	public void handleBeforeRollback(RdbDaoEvent event) {
	}

	@Override
	public void handleException(RdbDaoEvent event, Throwable cause) {
	}

	@Override
	public void handleTransantionEnd(RdbDaoEvent event) {
	}

	@Override
	public void handleTransantionStart(RdbDaoEvent event) {
	}

	@Override
	public void handleRelease(RdbDaoEvent event) {
	}
}
