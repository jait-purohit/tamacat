/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log;

/**
 * <p>The interface of diagnostic context for logging.
 * 
 * <ul>
 *   <li>Nested diagnostic context.(NDC)</li>
 *   <li>Mapped diagnostic context.(MDC)</li>
 * </ul>
 */
public interface DiagnosticContext {

	void setNestedContext(String data);
	
	void setMappedContext(String key, String data);
	
	void remove();
	
	void remove(String key);
}
