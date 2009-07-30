/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.tamacat.httpd.core.HttpEngine;

public class Httpd {

	static final String XML = "components.xml";
	
	public static void main(String[] args) {
		XmlBeanFactory di = new XmlBeanFactory(new ClassPathResource(XML));
		if (di == null) throw new IllegalArgumentException(XML + " is not found.");
		HttpEngine server = (HttpEngine) di.getBean("server");
		if (server == null) throw new IllegalArgumentException();
		server.start();
	}
}
