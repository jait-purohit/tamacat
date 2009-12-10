/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.EventListener;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class LoggingEventListener implements EventListener {

	static final Log LOG = LogFactory.getLog(LoggingEventListener.class);
	
	@Override
	public void connectionClosed(NHttpConnection conn) {
        LOG.debug("Connection closed: " + conn);
	}

	@Override
	public void connectionOpen(NHttpConnection conn) {
        LOG.debug("Connection open: " + conn);
	}

	@Override
	public void connectionTimeout(NHttpConnection conn) {
        LOG.debug("Connection timeout: " + conn);
	}

	@Override
	public void fatalIOException(IOException ex, NHttpConnection conn) {
        LOG.error("I/O error: " + ex.getMessage());
	}

	@Override
	public void fatalProtocolException(HttpException ex, NHttpConnection conn) {
        LOG.error("HTTP error: " + ex.getMessage());
	}

}
