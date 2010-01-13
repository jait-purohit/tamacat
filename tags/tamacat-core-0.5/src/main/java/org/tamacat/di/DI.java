/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di;

import org.tamacat.di.define.BeanDefine;
import org.tamacat.di.define.BeanDefineMap;
import org.tamacat.di.impl.DIContainerFactory;
import org.tamacat.di.impl.TamaCatDIContainer;
import org.tamacat.util.ClassUtils;

/**
 * DI is creates {@link DIContainer}s from {@link BeanDefine}s 
 * or configuration file(XML).
 */
public final class DI {

	/**
	 * Creates an {@link DIContainer} for the given set of configuration file(XML).
	 * @param defines Configuration file(XML) in CLASSPATH.
	 * @return {@link DIContainer}
	 */
	public static DIContainer configure(String xml) {
		return DIContainerFactory.getInstance(xml);
	}
	
	/**
	 * Creates an {@link DIContainer} for the given set of defines.
	 * @param defines Array of {@link BeanDefine}.
	 * @return {@link DIContainer}
	 */
	public static DIContainer configure(BeanDefine... defines) {
		BeanDefineMap defineMap = new BeanDefineMap();
		for (BeanDefine def : defines) {
			defineMap.add(def);
		}
		return new TamaCatDIContainer(defineMap, ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Creates an {@link DIContainer} for the given set of defines.
	 * @param defines BeanDefineMap, such as Map of {@link BeanDefine}.
	 * @return {@link DIContainer}
	 */
	public static DIContainer configure(BeanDefineMap defines) {
		return new TamaCatDIContainer(defines, ClassUtils.getDefaultClassLoader());
	}
	
	private DI() {}
}
