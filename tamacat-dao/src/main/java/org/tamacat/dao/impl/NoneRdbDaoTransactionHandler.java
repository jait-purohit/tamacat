/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import org.tamacat.dao.event.RdbDaoEvent;
import org.tamacat.dao.event.RdbDaoTransactionHandler;

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
