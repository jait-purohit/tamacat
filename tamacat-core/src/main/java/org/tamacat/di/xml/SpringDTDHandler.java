/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.di.xml;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class SpringDTDHandler implements DTDHandler {
	static final Log LOG = LogFactory.getLog(SpringDTDHandler.class);
	
	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		//LOG.trace("notationDecl()");
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		//LOG.trace("unparsedEntityDecl()");
	}
}
