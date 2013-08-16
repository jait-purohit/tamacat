/*
 * Copyright (c) 2007, tamacat.org
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
import org.tamacat.util.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

public class SpringBeanDefineHandler extends DefaultHandler2 implements BeanDefineHandler {

	/* Name of XML Tag or Attribute. */
	protected static final String BEAN = "bean";
	protected static final String ID = "id";
	protected static final String NAME = "name";
	protected static final String CLASS = "class";
	protected static final String SINGLETON = "singleton";
	protected static final String SCOPE = "scope";
	protected static final String PROPERTY = "property";
	protected static final String REF = "ref";
	protected static final String VALUE = "value";
	protected static final String NULL = "null";
	protected static final String TYPE = "type";
	protected static final String CONSTRUCTOR_ARG = "constructor-arg";
	protected static final String INIT_METHOD = "init-method";
	protected static final String FACTORY_METHOD = "factory-method";
	//any method support. (original)
	protected static final String METHOD_MODE = "mode";

	protected Log logger;
	protected BeanDefineMap beans;

	private BeanConstructorParam arg;
	protected BeanDefineParam ref;
	protected BeanDefineParam prop;

	protected BeanDefine bean;
	protected String nameBuffer;
	protected String modeBuffer;
	protected StringBuilder valueBuffer;

	protected boolean isConstrctor;
	protected ClassLoader loader;
	protected String xml;

	public SpringBeanDefineHandler() {}

	public void setLogger(Log logger) {
		this.logger = logger;
	}

	@Override
	public void setClassLoader(ClassLoader loader) {
		this.loader = loader;
	}

	protected ClassLoader getClassLoader() {
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
			reader.parse(new InputSource(IOUtils.getInputStream(xml, getClassLoader())));
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

	protected void clear() {
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

	protected void startBean(Attributes attributes) {
		bean = new BeanDefine();
		bean.setId(attributes.getValue(ID));
		bean.setAliases(attributes.getValue(NAME));
		bean.setType(ClassUtils.forName(attributes.getValue(CLASS), getClassLoader()));
		//factory-method
		String factoryMethod = attributes.getValue(FACTORY_METHOD);
		bean.setFactoryMethod(factoryMethod);

		String scope = attributes.getValue(SCOPE);
		if (scope != null) {
			bean.setSingleton(SINGLETON.equalsIgnoreCase(scope));
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

	//protected void startInnerBean(Attributes attributes) {
	//	bean = new DefaultBeanDefine();
	//	bean.setType(ClassUtils.forName(attributes.getValue(CLASS), getClassLoader()));
	//	bean.setSingleton(false); //always scoped as prototype
	//}

	protected void startProperty(Attributes attributes) {
		nameBuffer = attributes.getValue(NAME);
		modeBuffer = attributes.getValue(METHOD_MODE);
	}

	protected void startRef(Attributes attributes) {
		if (isConstrctor) {
			arg.setRefId(attributes.getValue(BEAN));
		} else {
			ref = new BeanDefineParam();
			ref.setName(nameBuffer, modeBuffer);
			ref.setRefId(attributes.getValue(BEAN));
		}
	}

	protected void startConstructorArg(Attributes attributes) {
		arg = new BeanConstructorParam();
		arg.setType(attributes.getValue(TYPE));
		isConstrctor = true;
	}

	protected void startValue(Attributes attributes) {
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

	protected void endBean() {
		beans.put(bean.getId(), bean);
		bean = null;
	}

	//protected void endInnerBean() {
	//	beans.put(bean.getId(), bean);
	//}

	protected void endProperty() {
		if (prop != null) { //<ref bean="xxx" />
			bean.getPropertyList().add(prop);
			prop = null;
		}
	}

	protected void endValue() {
		if (isConstrctor) {
			arg.setValue(getValueBuffer());
		} else {
			prop.setName(nameBuffer, modeBuffer);
			prop.setValue(getValueBuffer());
			nameBuffer = null;
			modeBuffer = null;
			valueBuffer = new StringBuilder();
		}
	}

	protected void endConstructorArg() {
		bean.addConstructorArgs(arg);
		isConstrctor = false;
		arg = null;
	}

	protected void endRef() {
		if (isConstrctor) {
			//none.
		} else {
			bean.getPropertyList().add(ref);
			ref = null;
		}
	}

	protected void endNull() {
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
