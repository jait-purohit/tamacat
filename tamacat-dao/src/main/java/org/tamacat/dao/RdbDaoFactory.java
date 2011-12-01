/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.di.DIContainerException;
import org.tamacat.util.ClassUtils;

public class RdbDaoFactory {

	static DIContainer di = DI.configure("orm.xml");
	
	@SuppressWarnings("unchecked")
	public static <T>T getRdbDao(Class<T> type) {
		try {
			return (T) di.getBean(type.getName());
		} catch (DIContainerException e) {
			return ClassUtils.newInstance(type);
		}
	}
}
