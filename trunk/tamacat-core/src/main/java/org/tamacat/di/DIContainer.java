/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di;

import java.util.List;

public interface DIContainer {

    public Object getBean(String id);
    public <T>T getBean(String id, Class<T> type);

    //public <T>List<T> getInstanceOfClass(Class<T> type);
    public <T>List<T> getInstanceOfType(Class<T> type);
}
