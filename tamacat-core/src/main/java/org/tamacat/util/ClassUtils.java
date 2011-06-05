/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.beans.IntrospectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities of Class and Method.
 */
public abstract class ClassUtils {

    static
      public ClassLoader getDefaultClassLoader() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {	}
        if (loader == null) {
            return ClassUtils.class.getClassLoader();
        } else {
            return loader;
        }
    }

    static
      public URL getURL(String path) {
        return getDefaultClassLoader().getResource(path);
    }

    static
      public URL getURL(String path, ClassLoader loader) {
        return loader.getResource(path);
    }
    
    static
      public <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            return null; //throw new ClassUtilsException(e);
        }
    }

    static
      public <T> T newInstance(Class<T> type, Class<?>[] argsTypes, Object... args) {
        try {
            Constructor<T> c = type.getConstructor(argsTypes);
            return c.newInstance(args);
        } catch (Exception e) {
            return null; //throw new ClassUtilsException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static
      public <T> T newInstance(Class<T> type, Object... args) {
    	if (args == null) {
    		return newInstance(type);
    	}
        T instance = null;
        Constructor<?>[] cons = type.getConstructors();
        for (Constructor<?> c : cons) {
            Class<?>[] types = c.getParameterTypes();
            if (types.length == args.length) {
                try {
                    instance = (T) c.newInstance(args);
                    break;
                } catch (Exception e) {
                }
            }
        }

        if (instance == null) {
        	throw new ResourceNotFoundException("Class Not found: " + type);
        }
        return instance;
    }
    
    static
      public Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            return null;//throw new ClassUtilsException(e);
        }
    }

    static
      public Class<?> forName(String className, ClassLoader loader) {
    	if (loader == null) return forName(className);
        try {
        	//return Class.forName(className, true, loader);
            return loader.loadClass(className);
        } catch (Exception e) {
            return null;//throw new ClassUtilsException(e);
        }
    }

    static
      public <T> Method getMethod(Class<T> type, String methodName, Class<?>... params) {
    	if (type != null && methodName != null && methodName.length() > 0) {
    		try {
    			if (params != null && params.length > 0 && params[0] != null) {
    				return type.getMethod(methodName, params);
    			} else {
    				return type.getMethod(methodName);
    			}
        	} catch (NoSuchMethodException e) {
        	}
    	}
        return null;
    }

    static
      public <T> Method getDeclaredMethod(Class<T> type, String methodName, Class<?>... params) {
    	if (type != null && methodName != null && methodName.length() > 0) {
    		try {
    			return type.getDeclaredMethod(methodName, params);
    		} catch (NoSuchMethodException e) {
    		}
    	}
        return null;
    }

    static
      public Method[] findMethods(Class<?> type, String methodName) {
    	if (type == null || methodName == null || methodName.length() == 0) {
    		return null;
    	}
        try {
            Method[] methods = type.getMethods();
            HashSet<Method> findMethods = new HashSet<Method>();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    findMethods.add(method);
                }
            }
            if (findMethods.size() > 0) {
            	return findMethods.toArray(new Method[findMethods.size()]);
            }
        } catch (Exception e) {
        }
        return null;
    }

    static
      public Method[] findDeclaredMethods(Class<?> type, String methodName) {
    	if (type == null || methodName == null || methodName.length() == 0) {
    		return null;
    	}
        try {
            Method[] methods = type.getDeclaredMethods();
            HashSet<Method> findMethods = new HashSet<Method>();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    findMethods.add(method);
                }
            }
            if (findMethods.size() > 0) {
            	return findMethods.toArray(new Method[findMethods.size()]);
            }
        } catch (Exception e) {
        }
        return null;
    }

    static
      public Method getStaticMethod(Class<?> type, String methodName, Class<?>... params) {
        try {
            Method method = type.getDeclaredMethod(methodName, params);
            if ((method.getModifiers() & Modifier.STATIC) != 0) {
                return method;
            }
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    static
      public <T>Object invoke(Method method, T instance, Object... params) {
        try {
        	if (params == null) {
        		return method.invoke(instance);
        	} else {
        		return method.invoke(instance, params);
        	}
        } catch (Exception e) {
            return null;
            //throw new ClassUtilsException(e);
        }
    }

    static
      public Method searchMethod(Method[] methods, String methodName, Class<?>... paramType) {
        if (methods == null || methods.length == 0) return null;
        for (Method method : methods) {
            Class<?>[] m = method.getParameterTypes();
            if (methods.length == 1 && m.length == 0) {
            	return method;
            } else if (m.length == paramType.length) {
                for (int i=0; i<paramType.length; i++) {
                    if (m[i].equals(paramType[i])) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    static
      public Method searchMethod(Class<?> type, String methodName, Class<?>... paramType) {
    	if (paramType != null && paramType.length > 0 && paramType[0] != null) {
    		Method m = getMethod(type, methodName, paramType);
    		if (m != null) return m;
    		
    		//paramType is interface?
        	Set<Class<?>> p = getAllClasses(new HashSet<Class<?>>(), paramType[0]);
	        //Class<?>[] p = paramType[0].getDeclaredClasses();
	        if (p != null) {
	        	for (Class<?> refIF : p) {
	        		m = getMethod(type, methodName, refIF);
	            	if (m != null) return m;
	        	}
	        }
	        //paramType is Object.class
	        Class<?>[] argsType = new Class<?>[paramType.length];
	        for (int i=0; i<paramType.length; i++) {
	            argsType[i] = Object.class;
	        }
	        return getMethod(type, methodName, argsType);
    	} else {
    		return getMethod(type, methodName);
    	}
    }
	
	static
	  Set<Class<?>> getAllClasses(Set<Class<?>> classes, Class<?> type) {
		Class<?>[] list = type.getClasses();
		for (Class<?> t : list) {
			classes = getAllClasses(classes, t);
			classes.add(t);
		}
		Class<?>[] interfaces = type.getInterfaces();
		for (Class<?> t : interfaces) {
			classes = getAllClasses(classes, t);
			classes.add(t);
		}
		Class<?> superClass = type.getSuperclass();
		if (superClass != null && superClass != Object.class) {
			Set<Class<?>> superClasses = getAllClasses(classes, superClass);
			for (Class<?> t : superClasses) {
				classes = getAllClasses(classes, t);
				classes.add(t);
			}
		}
		return classes;
	}
	
    static
      public Method getSetterMethod(String propertyName, Class<?> target) {
  	    try {
			return new java.beans.PropertyDescriptor(propertyName, target).getWriteMethod();
		} catch (IntrospectionException e) {
			throw new ClassUtilsException(e);
		}
    }
    
    static
      public Method getGetterMethod(String propertyName, Class<?> target) {
  	    try {
			return new java.beans.PropertyDescriptor(propertyName, target).getReadMethod();
		} catch (IntrospectionException e) {
			throw new ClassUtilsException(e);
		}
    }
    
    static
      public String getAdderMethodName(String propertyName) {
        return "add" + getCamelCaseName(propertyName);
    }
    
    static
      public String getRemoveMethodName(String propertyName) {
        return "remove" + getCamelCaseName(propertyName);
    }
    
    static
      public String getSetterMethodName(String propertyName) {
        return "set" + getCamelCaseName(propertyName);
    }

    static
      public String getGetterMethodName(String propertyName) {
        return "get" + getCamelCaseName(propertyName);
    }

    static
      public String getCamelCaseName(String name) {
    	if (name.length() <= 1) return name;
        return name.substring(0,1).toUpperCase() + name.substring(1, name.length());
    }

    static
      public boolean isTypeOf(Class<?> src, Class<?> dist) {
        if (src == dist) {
            return true;
        } else {
        	if (src != null && dist != null) {
        		return dist.isAssignableFrom(src);
        	} else {
        		return false;
        	}
        }
    }
    
    static
      public Type[] getGenericType(Class<?> target) {
    	if (target == null) return new Type[0];
    	Type[] types = target.getGenericInterfaces();
    	if (types.length > 0) {
    		return types;
    	}
    	Type type = target.getGenericSuperclass();
    	if (type != null) {
    		if (type instanceof ParameterizedType) {
    			return new Type[] { type };
    		}
    	}
    	return new Type[0];
    }
    
    static
      public ParameterizedType getParameterizedType(Class<?> target) {
    	Type[] types = getGenericType(target);
    	if (types.length > 0&& types[0] instanceof ParameterizedType) {
    		return (ParameterizedType) types[0];
    	}
    	return null;
    }
    
    static
      public Type[] getParameterizedTypes(Class<?> target) {
  	    Type[] types = getGenericType(target);
  	    if (types.length > 0&& types[0] instanceof ParameterizedType) {
  		    return ((ParameterizedType) types[0]).getActualTypeArguments();
  	    }
  	    return null;
    }
}
