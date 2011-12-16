/*
 * Copyright (c) 2009, TamaCat.org
 * All ri
import java.lang.annotation.RetentionPolicy;
ghts reserved.
 */
package org.tamacat.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptUtils {

	@Target({ METHOD, FIELD })
	@Retention(RUNTIME)
	@interface JavaScript{
		String value();
	}
	
	public static ScriptEngine getScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        return engine;
	}
	
	public static Object eval(Method method) throws ScriptException {
		JavaScript script = method.getAnnotation(JavaScript.class);
		if (script == null) return null;
		return eval(script);
	}
	
	public static Object eval(JavaScript script) throws ScriptException {
		return getScriptEngine().eval(script.value());
	}
}
