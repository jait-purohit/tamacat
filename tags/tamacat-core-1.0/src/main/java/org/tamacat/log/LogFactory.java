/*
 * Copyright (c) 2007, tamacat.org
 * All rights reserved.
 */
package org.tamacat.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.tamacat.log.impl.JDKLogger;
import org.tamacat.log.impl.Log4jDiagnosticContext;
import org.tamacat.log.impl.Log4jLogger;
import org.tamacat.log.impl.NoneDiagnosticContext;
import org.tamacat.log.impl.SimpleLogger;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.PropertyUtils;

/**
 * Usage: 
 * {@code
 *   static final Log LOG = LogFactory.getLog(Target.class);
 * } 
 */
public class LogFactory {

    private static final String LOGGING_PROPERTIES_FILE = "logging.properties";
    private static final String KEY = "org.tamacat.log.Log";

    private static final Map<String, Log> manager = new HashMap<String, Log>();

    public synchronized static Log getLog(String categoryName) {
        Log logger = manager.get(categoryName);
        if (logger == null) {
            try {
                logger = SELF.loadLogger(categoryName);
                if (logger != null) manager.put(categoryName, logger);
            } catch (Exception e) {
                new JDKLogger(LogFactory.class.getName()).error(e.getMessage(), e);
            }
        }
        return logger;
    }
    
    public synchronized static Log getLog(Class<?> className) {
        return getLog(className.getName());
    }

    static final String LOG4J_CLASS = "org.apache.log4j.Logger";
    private LogFactory(){}
    static LogFactory SELF = new LogFactory();

    protected Log loadLogger(String name) {
        Log logger = null;
        try {
            Properties props = PropertyUtils.getProperties(LOGGING_PROPERTIES_FILE);
            if (props != null) {
                String className = props.getProperty(KEY);
                Class<?> clazz = ClassUtils.forName(className);
                logger = (Log) ClassUtils.newInstance(clazz, String[].class, name);
                if (logger != null) return logger;
            }
        } catch (Exception e) {
        }
        Class<?> loggerClass = ClassUtils.forName(LOG4J_CLASS);
        if (loggerClass != null) {
            return new Log4jLogger(name);
        } else {
            return new SimpleLogger();
        }
    }
    
    public static DiagnosticContext getDiagnosticContext(Log logger) {
    	if (logger instanceof Log4jLogger) {
    		return new Log4jDiagnosticContext();
    	} else {
    		return new NoneDiagnosticContext();
    	}
    }
}
