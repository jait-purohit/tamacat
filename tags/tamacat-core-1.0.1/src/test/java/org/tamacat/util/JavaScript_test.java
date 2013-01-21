/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class JavaScript_test {

	@JavaScript(
			"function gerClosure(_this) {" + 
				"var inner = 2;"+ 
				
				"function invoke(param) {"+ 
				"	return inner * _this.x * param;"+ 
				"}"+ 
				
				"return invoke;"+ 
			"}" +	
			"var invoke = gerClosure(_this);" + 
			"String(parseInt(invoke(_param)));  "     
			)
			
	final static String SCRIPT = "print('Test');\n";
	
	@Target({ METHOD, FIELD })
	@Retention(RUNTIME)
	@interface JavaScript{
		String value();
	}
}
