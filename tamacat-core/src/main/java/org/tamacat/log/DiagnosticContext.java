/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log;

public interface DiagnosticContext {

	void setNestedContext(String data);
	
	void setMappedContext(String key, String data);
	
	void remove();
	
	void remove(String key);
}
