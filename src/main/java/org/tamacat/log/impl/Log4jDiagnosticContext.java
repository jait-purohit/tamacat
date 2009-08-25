/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log.impl;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.tamacat.log.DiagnosticContext;

public class Log4jDiagnosticContext implements DiagnosticContext {

	boolean useNDC;
	boolean useMDC;
	
	public void setMappedContext(String key, String data) {
		MDC.put(key, data);
		useMDC = true;
	}

	public void setNestedContext(String data) {
		NDC.push(data);
		useNDC = true;
	}

	public void remove() {
		if (useNDC) NDC.remove();
	}
	
	public void remove(String key) {
		if (useMDC) MDC.remove(key);
	}
}
