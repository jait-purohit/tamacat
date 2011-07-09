/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log.impl;

import java.util.Set;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.tamacat.log.DiagnosticContext;

public class Log4jDiagnosticContext implements DiagnosticContext {

	@Override
	public void setMappedContext(String key, String data) {
		MDC.put(key, data);
	}

	@Override
	public void setNestedContext(String data) {
		NDC.push(data);
	}

	@Override
	public void remove() {
		NDC.remove();
	}
	
	@Override
	public void remove(String key) {
		MDC.remove(key);
	}
	
	protected Set<?> keySet() {
		return MDC.getContext().keySet();
	}
}
