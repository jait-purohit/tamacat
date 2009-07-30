/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log;

/**
 * Log interface supports a basic logging operation.
 */
public interface Log {
    Object getOriginalLogger();

    void fatal(Object message, Throwable e);
    void error(Object message, Throwable e);
    void warn(Object message, Throwable e);
    
    void fatal(Object message);
    void error(Object message);
    void warn(Object message);
    void info(Object message);
    void debug(Object message);
    void trace(Object message);
    
    void fatal(Object message, String... args);
    void error(Object message, String... args);
    void warn(Object message, String... args);
    void info(Object message, String... args);
    void debug(Object message, String... args);
    void trace(Object message, String... args);

    boolean isFatalEnabled();
    boolean isErrorEnabled();
    boolean isWarnEnabled();
    boolean isInfoEnabled();
    boolean isDebugEnabled();
    boolean isTraceEnabled();
}
