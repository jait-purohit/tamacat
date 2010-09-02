/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.groovy.test;

public class CoreFactory {

    public static Core createCore() {
        return new DBCore();
    }
}
