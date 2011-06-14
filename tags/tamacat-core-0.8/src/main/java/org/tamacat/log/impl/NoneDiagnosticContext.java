/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log.impl;

import org.tamacat.log.DiagnosticContext;

public class NoneDiagnosticContext implements DiagnosticContext {

	public void remove() {
	}

	public void remove(String key) {
	}

	public void setMappedContext(String key, String data) {
	}

	public void setNestedContext(String data) {
	}
}
