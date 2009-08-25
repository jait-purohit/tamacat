/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.xml;

import java.io.IOException;

import org.tamacat.util.ClassUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SpringEntityResolver implements EntityResolver {

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		
		String[] d = systemId.split("/");
		String dtd = "org/tamacat/di/xml/" + d[d.length-1];
		return new InputSource(ClassUtils.getStream(dtd));
	}
}
