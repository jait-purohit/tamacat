/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

	public static String getStackTrace(Throwable e) {
		StringWriter out = new StringWriter();
		PrintWriter w = new PrintWriter(out);
		e.printStackTrace(w);
		w.flush();
		return out.toString();
	}
	
	public static boolean isRuntime(Exception e) {
		return e != null && e instanceof RuntimeException;
	}
	
	public static Throwable getCauseException(Exception e) {
		if (e == null) return null;
		Throwable cause = e.getCause();
		if (cause != null) return cause;
		else return e;
	}
}
