/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import org.tamacat.dao.event.RdbDaoEvent;
import org.tamacat.dao.event.RdbDaoTransactionHandler;
import org.tamacat.log.LogFactory;

public class LogRdbDaoTransactionHandler implements RdbDaoTransactionHandler {

	@Override
	public void handleAfterCommit(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug("commit.");
		} catch (Exception e) {
		}
	}

	@Override
	public void handleAfterRollback(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug("rollback.");
		} catch (Exception e) {
		}
	}

	@Override
	public void handleBeforeCommit(RdbDaoEvent event) {
	}

	@Override
	public void handleBeforeRollback(RdbDaoEvent event) {
	}

	@Override
	public void handleException(RdbDaoEvent event, Throwable cause) {
		try {
			LogFactory.getLog(event.getCallerDao()).error(event.getQuery(), cause);
		} catch (Exception e) {
		}
	}

	@Override
	public void handleRelease(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug("release.");
		} catch (Exception e) {
		}
	}

	@Override
	public void handleTransantionEnd(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug("end.");
		} catch (Exception e) {
		}
	}

	@Override
	public void handleTransantionStart(RdbDaoEvent event) {
		try {
			LogFactory.getLog(event.getCallerDao()).debug("start.");
		} catch (Exception e) {
		}
	}
}
