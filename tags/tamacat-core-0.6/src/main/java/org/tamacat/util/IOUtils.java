/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;

public class IOUtils {

	static
	  public void close(Object target) {
		if (target != null) {
			if (target instanceof Closeable) {
				close((Closeable)target);
			} else {
				try {
					Method closable = ClassUtils.searchMethod(
							target.getClass(), "close", (Class[])null);
					if (closable != null) closable.invoke(target);
				} catch (Exception e) {
				}
			}
		}
	}
	
	static
	  public void close(Closeable closable) {
	    try {
			if (closable != null) closable.close();
		} catch (IOException e) {
		}
	}
}
