/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.lang.reflect.Method;

public abstract class ReflectionUtils {

    static
      public Method setParameters(
            Object instance, String methodName, Object... params) {
	  	if (instance == null) return null;
	  	Method method = null;
	  	if (params == null) {
		    method = ClassUtils.searchMethod(instance.getClass(), methodName);
	  	} else {
	  		Class<?>[] paramTypes = new Class[params.length];
	  		for (int i=0; i<params.length; i++) {
	  			paramTypes[i] = params[i].getClass();
	  		}
	  		method = ClassUtils.searchMethod(
	    		instance.getClass(), methodName, paramTypes);
	  	}
	    if (method == null) throw new ClassUtilsException("method is null.");
	    ClassUtils.invoke(method, instance, params);
	    return method;
	}
}
