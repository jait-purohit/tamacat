/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.html;

public class ConvertData {
	
	private final boolean converted;
	private final String data;

	public ConvertData(String data, boolean converted) {
		this.data = data;
		this.converted = converted;
	}

	public String getData() {
		return data;
	}

	public boolean isConverted() {
		return converted;
	}
}
