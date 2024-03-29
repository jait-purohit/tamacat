/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.tamacat.di.define.BeanDefineParam;
import org.tamacat.util.ClassUtils;

public class PropertyValueHandler {

    static
      private final HashMap<Class<?>, StringValueConverter<?>> CONVERTERS = new HashMap<>();

    static
      public final void register(Class<?> type, StringValueConverter<?> converter) {
        CONVERTERS.put(type, converter);
    }

    static {
        register(String.class, new StringConverter());
        register(String[].class, new StringArrayConverter());
        register(Integer.class, new IntegerConverter());
        register(Long.class, new LongConverter());
        register(Float.class, new FloatConverter());
        register(Double.class, new DoubleConverter());
        register(Character.class, new CharacterConverter());
        register(Boolean.class, new BooleanConverter());
        register(Class.class, new ClassConverter());
        register(Class[].class, new ClassArrayConverter());
        register(Object.class, new ObjectConverter());
    }

    static
      final class StringConverter implements StringValueConverter<String> {
        public Class<String> getType() {
            return String.class;
        }
        public String convert(String param) {
            return param;
        }
    }

    static
      final class StringArrayConverter implements StringValueConverter<String[]> {
        public Class<String[]> getType() {
            return String[].class;
        }
        public String[] convert(String param) {
            return new String[] {param};
        }
    }

    static
      final class IntegerConverter implements StringValueConverter<Integer> {
        public Class<Integer> getType() {
            return Integer.TYPE;
        }
        public Integer convert(String param) {
            return Integer.parseInt(param);
        }
    }

    static
      final class LongConverter implements StringValueConverter<Long> {
        public Class<Long> getType() {
            return Long.TYPE;
        }
        public Long convert(String param) {
            return Long.parseLong(param);
        }
    }

    static
      final class FloatConverter implements StringValueConverter<Float> {
        public Class<Float> getType() {
            return Float.TYPE;
        }
        public Float convert(String param) {
            return Float.parseFloat(param);
        }
    }

    static
      final class DoubleConverter implements StringValueConverter<Double> {
        public Class<Double> getType() {
            return Double.TYPE;
        }
        public Double convert(String param) {
            return Double.parseDouble(param);
        }
    }

    static
      final class CharacterConverter implements StringValueConverter<Character> {
        public Class<Character> getType() {
            return Character.TYPE;
        }
        public Character convert(String param) {
            return Character.valueOf(param.charAt(0));
        }
    }

    static
      final class BooleanConverter implements StringValueConverter<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.TYPE;
        }
        public Boolean convert(String param) {
            if ("true".equalsIgnoreCase(param)) {
                return Boolean.TRUE.booleanValue();
            } else if ("false".equalsIgnoreCase(param)) {
                return Boolean.FALSE.booleanValue();
            } else {
                throw new RuntimeException("can not convert boolean.");
            }
        }
    }

    @SuppressWarnings("rawtypes")
	static
      final class ClassConverter implements StringValueConverter<Class> {
		public Class<Class> getType() {
            return Class.class;
        }
        public Class<?> convert(String param) {
            return ClassUtils.forName(param);
        }
    }

    @SuppressWarnings("rawtypes")
	static
      final class ClassArrayConverter implements StringValueConverter<Class[]> {
        public Class<Class[]> getType() {
            return Class[].class;
        }
        public Class<?>[] convert(String param) {
            return new Class[] {ClassUtils.forName(param)};
        }
    }

    static
      final class ObjectConverter implements StringValueConverter<Object> {
        public Class<Object> getType() {
            return Object.class;
        }
        public Object convert(String param) {
            return param;
        }
    }
      
    private Class<?> type;
    private BeanDefineParam param;

    public PropertyValueHandler(Class<?> type, BeanDefineParam param) {
        this.type = type;
        this.param = param;
    }

    public <T>Object resolvParameterValue(T instance) {
        if (param.isRegistMethod()) {
            StringValueConverter<?> cv = CONVERTERS.get(param.getParamType());
            if (cv != null) {
                return ClassUtils.invoke(
                    param.getMethod(), instance, cv.convert(param.getValue()));
            }
        }
        Method[] methods = ClassUtils.findMethods(type, param.getName());
        for (StringValueConverter<?> cv : CONVERTERS.values()) {
            try {
                Object value = cv.convert(param.getValue());
                Method m = ClassUtils.searchMethod(methods, param.getName(), cv.getType());
                if (m != null) { //success.
                    @SuppressWarnings("unused")
                    Object result = ClassUtils.invoke(m, instance, value);
                    if (param.isRegistMethod() == false) {
                    	param.setStringValueConverter(cv);
                        param.setMethod(m);
                        param.setParamType(cv.getType());
                    }
                    return instance;
                }
            } catch (Exception e) {
            }
        }
        return instance;
    }
}
