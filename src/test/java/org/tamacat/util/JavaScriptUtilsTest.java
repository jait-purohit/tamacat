package org.tamacat.util;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.tamacat.util.JavaScript_test.JavaScript;

public class JavaScriptUtilsTest {
	
	@JavaScript("var test = true;")
	public void execute() { 
	}
			
	@Test
	public void testEvalMethod() throws ScriptException {
		Method method = ClassUtils.getMethod(JavaScriptUtilsTest.class, "execute", (Class<?>[]) null);
		ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
		JavaScript script = method.getAnnotation(JavaScript.class);
		assertNull(engine.eval(script.value()));
	}

	@Test
	public void testEvalJavaScript() throws ScriptException {
		Method method = ClassUtils.getMethod(JavaScriptUtilsTest.class, "execute", (Class<?>[]) null);
		JavaScript script = method.getAnnotation(JavaScript.class);
		assertNull(JavaScriptUtils.getScriptEngine().eval(script.value()));
	}
}
