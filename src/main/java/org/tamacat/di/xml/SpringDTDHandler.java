/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.xml;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class SpringDTDHandler implements DTDHandler {

	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		//System.out.println("notationDecl()");
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		//System.out.println("unparsedEntityDecl()");
	}
}
