package org.tamacat.banchmark.worker;

import org.apache.http.message.BasicHeader;

public class DefaultHeader extends BasicHeader {

	private static final long serialVersionUID = 1L;

	public DefaultHeader(String name, String value) {
		super(name, value);
	}

}
