/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.log.impl;

import org.tamacat.log.Log;
import org.tamacat.util.StringUtils;

/**
 * 
 * @param <T> output logging level.
 */
public abstract class AbstractLogger<T> implements Log {

    protected abstract T getFatalLevel();
    protected abstract T getErrorLevel();
    protected abstract T getWarnLevel();
    protected abstract T getInfoLevel();
    protected abstract T getDebugLevel();
    protected abstract T getTraceLevel();
    
    /**
     * To output the log according to the level,
     * this method is mounted in the subclass. 
     * @param level
     * @param message
     */
    protected abstract void log(T level, Object message);

    /**
     * To return the {@literal true} when it is enable to output it,
     * this method is mounted in the subclass.
     * @param level
     * @return true if the output is enable, false otherwise.
     */
    protected abstract boolean isEnabled(T level);
    
    /**
     * replace holder {@code ex, args[0] -> "${0}" }
     * @param level
     * @param message execute {@link java.lang.Object#toString()}
     * @param args
     */
    protected void log(T level, Object message, String... args) {
    	String _message = "";
    	if (message != null) _message = message.toString(); 
        for (int i=0; i<args.length; i++) {
            _message = _message.replace("${"+i+"}", args[i]);
        }
        log(level, _message);
    }

    @Override
    public void fatal(Object message) {
        log(getFatalLevel(), message);
    }

    @Override
    public void error(Object message) {
        log(getErrorLevel(), message);
    }

    @Override
    public void warn(Object message) {
        log(getWarnLevel(), message);
    }

    @Override
    public void info(Object message) {
        log(getInfoLevel(), message);
    }

    @Override
    public void debug(Object message) {
        log(getDebugLevel(), message);
    }

    @Override
    public void trace(Object message) {
        log(getTraceLevel(), message);
    }

    @Override
    public void fatal(Object message, String... args) {
        log(getFatalLevel(), message, args);
    }

    @Override
    public void error(Object message, String... args) {
        log(getErrorLevel(), message, args);
    }

    @Override
    public void warn(Object message, String... args) {
        log(getWarnLevel(), message, args);
    }

    @Override
    public void info(Object message, String... args) {
        log(getInfoLevel(), message, args);
    }

    @Override
    public void debug(Object message, String... args) {
        log(getDebugLevel(), message, args);
    }

    @Override
    public void trace(Object message, String... args) {
        log(getTraceLevel(), message, args);
    }

    @Override
    public boolean isFatalEnabled() {
        return isEnabled(getFatalLevel());
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(getErrorLevel());
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(getWarnLevel());
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(getInfoLevel());
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(getDebugLevel());
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(getTraceLevel());
    }

    @Override
    public void error(Object message, Throwable e) {
        error(message);
        error(StringUtils.getStackTrace(e));
    }

    @Override
    public void fatal(Object message, Throwable e) {
        fatal(message);
        fatal(StringUtils.getStackTrace(e));
    }

    @Override
    public void warn(Object message, Throwable e) {
        warn(message);
        warn(StringUtils.getStackTrace(e));
    }
}
