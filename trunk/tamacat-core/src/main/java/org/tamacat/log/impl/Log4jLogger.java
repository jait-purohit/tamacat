/*
 * Copyright (c) 2007, tamacat.org
 * All rights reserved.
 */
package org.tamacat.log.impl;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

public class Log4jLogger extends AbstractLogger<Priority> implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient org.apache.log4j.Logger delegate;
    private static Level TRACE_LEVEL;
    static final String callerFQCN = Log4jLogger.class.getName();

    private String name;

    static {
        try {
        	//TRACE supported to log4j-1.2.13 and over.
            TRACE_LEVEL = (Level) Level.class.getDeclaredField("TRACE").get(null);
        } catch (Throwable e) {
        	//It corresponds to log4j-1.2.12 or less.
            TRACE_LEVEL = Level.DEBUG;
        }
    }

    public Log4jLogger(String name) {
        this.name = name;
        delegate = createLog4jLogger(name);
    }

    static org.apache.log4j.Logger createLog4jLogger(String name){
        return org.apache.log4j.Logger.getLogger(name);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
        delegate = createLog4jLogger(name);
    }

    @Override
    protected void log(Priority level, Object message) {
        delegate.log(name, level, message, null);
    }

    @Override
    protected Level getDebugLevel() {
        return Level.DEBUG;
    }

    @Override
    protected Level getErrorLevel() {
        return Level.ERROR;
    }

    @Override
    protected Level getFatalLevel() {
        return Level.FATAL;
    }

    @Override
    protected Level getInfoLevel() {
        return Level.INFO;
    }

    @Override
    protected Level getTraceLevel() {
        return TRACE_LEVEL;
    }

    @Override
    protected Level getWarnLevel() {
        return Level.WARN;
    }

    @Override
    protected boolean isEnabled(Priority level) {
        return delegate.isEnabledFor(level);
    }

    public Object getOriginalLogger() {
        return delegate;
    }
}
