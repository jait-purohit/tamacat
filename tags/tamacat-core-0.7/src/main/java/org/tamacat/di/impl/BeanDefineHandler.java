/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.impl;

import org.tamacat.di.define.BeanDefineMap;

public interface BeanDefineHandler {

    void setClassLoader(ClassLoader loader);

    void setConfigurationFile(String path);

    BeanDefineMap getBeanDefines();
}
