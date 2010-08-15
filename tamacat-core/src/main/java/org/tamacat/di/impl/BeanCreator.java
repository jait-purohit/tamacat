/*
 * Copyright (c) 2007-2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tamacat.di.DIContainerException;
import org.tamacat.di.define.BeanConstructorParam;
import org.tamacat.di.define.BeanDefine;
import org.tamacat.di.define.BeanDefineMap;
import org.tamacat.di.define.BeanDefineParam;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.ClassUtilsException;
import org.tamacat.util.StringUtils;

/**
 * Create beans for DIContainer.
 */
public class BeanCreator {

    static final Map<String, BeanAdapter<Object>> beans
    	= new HashMap<String, BeanAdapter<Object>>();

    private final BeanDefineMap defines;

    private final ClassLoader loader;

    /**
     * Constructor for DIContainer
     * @param defines
     */
    BeanCreator(BeanDefineMap defines) {
        this.defines = defines;
        this.loader = getClass().getClassLoader();
        initDefines();
    }

    /**
     * Constructor for DIContainer
     * @param defines
     */
    BeanCreator(BeanDefineMap defines, ClassLoader loader) {
        this.defines = defines;
        this.loader = loader;
        initDefines();
    }

    BeanDefineMap getDefines() {
        return defines;
    }

    ClassLoader getClassLoader() {
        return loader;
    }

    /**
     * Get bean for DIContainer
     * @param id
     * @return Object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	synchronized <T> T getBean(String id, Class<T> type) {
        BeanDefine def = defines.get(id);
        if (def == null) {
            throw new DIContainerException("Error: [" + id + "] is not found in BeanDefines.");
        }

        //singleton instance
        if (def.isSingleton()) {
            //instance is null then create new instance.
            BeanAdapter<Object> adapter = beans.get(id);
            if (adapter == null) {
                T instance = newInstance(def);
                beans.put(id, new BeanAdapter(id, def.getType(), instance));
                return instance;
            } else {
                return (T) initializeInstance(def, adapter.getInstance());
            }
        } else { //Prototype, always new instance.
            return newInstance(def);
        }
    }

    /*=== private methods. ===*/

    /**
     * Merge aliases and id.
     */
    private void initDefines() {
        String[] keys = StringUtils.toStringArray(defines.keySet());
        for (String key : keys) {
            String[] aliases = defines.get(key).getAliases();
            if (aliases != null && aliases.length > 0) {
                for (String alias : aliases) {
                    if (defines.containsKey(alias) == false) {
                        defines.put(alias, defines.get(key));
                    }
                }
            }
        }
    }

    /**
     * If BeanDefine#useInitMethod() returns true,
     * then execute initialize method.
     * @param def
     * @param instance
     * @return instance
     */
    private <T> T initializeInstance(BeanDefine def, T instance) {
        if (def.useInitMethod()) {
            ClassUtils.invoke(def.getInitMethod(), instance, (Object[]) null);
        }
        return instance;
    }

    /**
     * New instance by BeanDefine.
     * ( Constructor Injection / FactoryMethod / Setter Injection )
     * @param def BeanDefine
     * @return new instance
     */
    @SuppressWarnings("unchecked")
    private <T extends Object> T newInstance(BeanDefine def) {
        T instance = null;
        //for Factory Method.
        if (def.getFactoryMethod() != null) {
            instance = (T) ClassUtils.invoke(def.getFactoryMethod(), (Object)null, (Object[])null);
            if (instance != null && (instance.getClass() != def.getType())) {
                //Convert the Factory Class -> Instance Class.
                def.setType(instance.getClass());
            }
        }
        //for Constructor Injection.
        if (def.getConstructorArgs().size() > 0) {
            //use constructor injection.
            instance = (T)newInstanceForConstructorInjection(def);
        } else {
            //use default constructor.
            instance = (T)initializeInstance(def, ClassUtils.newInstance(def.getType()));
        }
        //use setter injection.
        for (BeanDefineParam prop : def.getPropertyList()) {
        	if (prop == null) continue;
            if (prop.isRef()) {
                instance = setterInjection(instance, prop, def, getBean(prop.getRefId(), prop.getParamType()));
            } else {
                instance = setterInjectionByBasicType(instance, prop, def, prop.getValue());
            }
        }
        return instance;
    }

    /**
     * New instance by BeanDefine for Constructor Injection.
     * @param def
     * @return new instance
     */
    private Object newInstanceForConstructorInjection(BeanDefine def) {
        List<BeanConstructorParam> argsDef = def.getConstructorArgs();
        Class<?>[] argsTypes = new Class<?>[argsDef.size()];
        Object[] args = new Object[argsDef.size()];
        boolean isAutoTypes = false;
        for (int i=0; i<argsDef.size(); i++) {
            if (argsDef.get(i).isRef()) {
                argsTypes[i] = defines.get(argsDef.get(i).getRefId()).getType();
                args[i] = getBean(argsDef.get(i).getRefId(), argsTypes[i]);
            } else {
                args[i] = argsDef.get(i).getValue();
                //args type is not defined.
                if (argsDef.get(i).getType() == null) {
                    isAutoTypes = true;
                } else {
                    argsTypes[i] = ClassUtils.forName(argsDef.get(i).getType(), loader);
                }
            }
        }
        //execute constructor injection.
        if (isAutoTypes) {
            return initializeInstance(def, ClassUtils.newInstance(def.getType(), args));
        } else {
            return initializeInstance(def, ClassUtils.newInstance(def.getType(), argsTypes, args));
        }
    }

    /**
     * Setter injection method for parameter type equals Object (ref)
     * @param instance
     * @param prop
     * @param def
     * @param param
     * @return instance
     */
    private <T> T setterInjection(T instance, BeanDefineParam prop, BeanDefine def, Object param) {
        Method method = prop.getMethod();
        if (method == null) {
        	Class<?> p = param != null ? param.getClass() : null;
            method = ClassUtils.searchMethod(def.getType(), prop.getName(), p);
            if (method == null) throw new ClassUtilsException("method is null. [" + prop.getName() + ":" + param + "] in " + instance);
        }
        ClassUtils.invoke(method, instance, param);
        //Regist method cache.
        prop.setMethod(method);
        return instance;
    }

    /**
     * Setter injection method for parameter type is primitive or String.
     * TODO: overload, multiple parameters.
     * @param instance
     * @param prop
     * @param def
     * @param param
     * @return instance
     */
    @SuppressWarnings("unchecked")
    private <T extends Object> T setterInjectionByBasicType(
                T instance, BeanDefineParam prop,
                BeanDefine def, String param) {
        Method method = prop.getMethod();
        if (method != null) {
        	StringValueConverter<?> converter = prop.getStringValueConverter();
        	if (converter != null) {
        		ClassUtils.invoke(method, instance, converter.convert(param));
        	} else {
        		ClassUtils.invoke(method, instance, param);
        	}
            return instance;
        } else {
            PropertyValueHandler handler = new PropertyValueHandler(def.getType(), prop);
            return (T) handler.resolvParameterValue(instance);
        }
    }
}
