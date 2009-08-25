/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.xml;

import org.tamacat.di.DIContainerException;
import org.tamacat.di.define.BeanConstructorParam;
import org.tamacat.di.define.BeanDefine;
import org.tamacat.di.define.BeanDefineMap;
import org.tamacat.di.define.BeanDefineParam;
import org.tamacat.di.impl.BeanDefineHandler;
import org.tamacat.log.Log;
import org.tamacat.util.ClassUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

public class SpringBeanDefineHandler extends DefaultHandler2 implements BeanDefineHandler {

    /* Name of XML Tag or Attribute. */
    private static final String BEAN = "bean";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CLASS = "class";
    private static final String SINGLETON = "singleton";
    private static final String SCOPE = "scope";
    private static final String PROPERTY = "property";
    private static final String REF = "ref";    
    private static final String VALUE = "value";
    private static final String NULL = "null";
    private static final String TYPE = "type";
    private static final String CONSTRUCTOR_ARG = "constructor-arg";
    private static final String INIT_METHOD = "init-method";
    private static final String FACTORY_METHOD = "factory-method";
    //any method support. (original)
    private static final String METHOD_MODE = "mode";
    
    private Log logger;
    private BeanDefineMap beans;

    private BeanConstructorParam arg;
    private BeanDefineParam ref;
    private BeanDefineParam prop;

    private BeanDefine bean;
    private String nameBuffer;
    private String modeBuffer;
    private StringBuilder valueBuffer;

    private boolean isConstrctor;
    private ClassLoader loader;
    private String xml;

    public SpringBeanDefineHandler() {}

    void setLogger(Log logger) {
        this.logger = logger;
    }

    @Override
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    ClassLoader getClassLoader() {
        return (loader != null) ?
            loader : getClass().getClassLoader();
    }
    
    @Override
    public void setConfigurationFile(String xml) {
        this.xml = xml;
    }

    @Override
    public BeanDefineMap getBeanDefines() {
        try { //XML parse to List<BeanDefine>
        	XMLReader reader = XMLReaderFactory.createXMLReader();
        	reader.setContentHandler(this);
        	reader.setDTDHandler(new SpringDTDHandler());
        	reader.setEntityResolver(new SpringEntityResolver());
        	reader.setErrorHandler(this);
        	//reader.setFeature("http://xml.org/sax/features/validation",true);
        	//reader.setFeature("http://xml.org/sax/features/namespaces",true);
            reader.parse(new InputSource(ClassUtils.getStream(xml)));
        } catch (Exception e) {
            throw new DIContainerException(e);
        }
        return beans;
    }

    @Override
    public void startDocument() {
        beans = new BeanDefineMap();
    }

    @Override
    public void endDocument() {
        clear();
    }

    void clear() {
        arg = null;
        ref = null;
        prop = null;
        bean = null;
        nameBuffer = null;
        modeBuffer = null;
        valueBuffer = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
    	valueBuffer = new StringBuilder();
        if (name.equals(BEAN)) {
            //3.3.2.3. Inner beans support
            //if (isConstrctor) {
            //	startInnerBean(attributes);
            //} else {
                startBean(attributes);
            //}
        } else if (name.equals(PROPERTY)) {
            startProperty(attributes);
        } else if (name.equals(REF)) {
            startRef(attributes);
        } else if (name.equals(CONSTRUCTOR_ARG)) {
            startConstructorArg(attributes);
        } else if (name.equals(VALUE)) {
            startValue(attributes);
        } else if (name.equals(NULL)) {
            startValue(attributes);
        }
    }

    void startBean(Attributes attributes) {
        bean = new BeanDefine();
        bean.setId(attributes.getValue(ID));
        bean.setAliases(attributes.getValue(NAME));
        bean.setType(ClassUtils.forName(attributes.getValue(CLASS), getClassLoader()));
        //factory-method
        String factoryMethod = attributes.getValue(FACTORY_METHOD);
        bean.setFactoryMethod(factoryMethod);

        String scope = attributes.getValue(SCOPE);
        if (scope != null) {
            bean.setSingleton("singleton".equalsIgnoreCase(scope));
        } else {
        	String singleton = attributes.getValue(SINGLETON);
        	if (singleton != null) {
            	bean.setSingleton(Boolean.parseBoolean(singleton));
        	}
        }
        String initMethod = attributes.getValue(INIT_METHOD);
        if (initMethod != null && ! initMethod.equals("")) {
            bean.setInitMethod(initMethod);
        }
    }

    //void startInnerBean(Attributes attributes) {
    //	bean = new DefaultBeanDefine();
    //	bean.setType(ClassUtils.forName(attributes.getValue(CLASS), getClassLoader()));
    //	bean.setSingleton(false); //always scoped as prototype
    //}

    void startProperty(Attributes attributes) {
        nameBuffer = attributes.getValue(NAME);
        modeBuffer = attributes.getValue(METHOD_MODE);
    }

    void startRef(Attributes attributes) {
        if (isConstrctor) {
            arg.setRefId(attributes.getValue(BEAN));
        } else {
            ref = new BeanDefineParam();
            ref.setName(nameBuffer, modeBuffer);
            ref.setRefId(attributes.getValue(BEAN));
        }
    }

    void startConstructorArg(Attributes attributes) {
        arg = new BeanConstructorParam();
        arg.setType(attributes.getValue(TYPE));
        isConstrctor = true;
    }

    void startValue(Attributes attributes) {
        prop = new BeanDefineParam();
    }

//	@Override
//	public void startCDATA() {
//		System.out.println("startCDATA");
//	}

    @Override
    public void endElement(String uri, String localName, String name) {
        if (name.equals(BEAN)) {
            //if (isConstrctor) { //Inner Bean support
            //	endInnerBean();
            //} else {
                endBean();
            //}
        } else if (name.equals(PROPERTY)) {
            endProperty();
        } else if (name.equals(VALUE)) {
            endValue();
        } else if (name.equals(CONSTRUCTOR_ARG)) {
            endConstructorArg();
        } else if (name.equals(REF)) {
            endRef();
        } else if (name.equals(NULL)) {
            endNull();
        }
    }

    void endBean() {
        beans.put(bean.getId(), bean);
    }

    //void endInnerBean() {
    //	beans.put(bean.getId(), bean);
    //}

    void endProperty() {
        if (prop != null) { //<ref bean="xxx" />
        	bean.getPropertyList().add(prop);
        }
    }

    void endValue() {
        if (isConstrctor) {
            arg.setValue(getValueBuffer());
        } else {
            prop.setName(nameBuffer, modeBuffer);
            prop.setValue(getValueBuffer());
        }
    }
    void endConstructorArg() {
        bean.addConstructorArgs(arg);
        isConstrctor = false;
    }

    void endRef() {
        if (isConstrctor) {
            //none.
        } else {
            bean.getPropertyList().add(ref);
        }
    }

    void endNull() {
        valueBuffer = null;
        if (isConstrctor) {
            arg.setValue(null);
        } else {
            prop.setName(nameBuffer, modeBuffer);
            prop.setValue(null);
        }
    }

    @Override
    public void endCDATA() {
        //System.out.println("endCDATA");
    }
    
    private String getValueBuffer() {
    	if (valueBuffer == null) return null;
    	return valueBuffer.toString();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length);
        if (valueBuffer == null) valueBuffer = new StringBuilder();
        valueBuffer.append(value.trim());
    }

    @Override
    public void warning(org.xml.sax.SAXParseException e) {
        if (logger != null) {
            logger.warn("line: " + e.getLineNumber());
            logger.warn(e.getMessage());
        } else {
            System.err.println("WARNING: " + e.getMessage());
        }
    }

    @Override
    public void error(org.xml.sax.SAXParseException e) {
        if (logger != null) {
            logger.error("line: " + e.getLineNumber());
            logger.error(e.getMessage());
        } else {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void fatalError(org.xml.sax.SAXParseException e) {
        if (logger != null) {
            logger.fatal("line: " + e.getLineNumber());
            logger.fatal(e.getMessage());
        } else {
            System.err.println("FATAL: " + e.getMessage());
        }
    }
}
