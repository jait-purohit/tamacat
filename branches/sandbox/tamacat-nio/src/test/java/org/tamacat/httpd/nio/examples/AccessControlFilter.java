/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio.examples;

import org.tamacat.httpd.filter.AbstractAccessControlFilter;

public class AccessControlFilter extends AbstractAccessControlFilter {

	@Override
	protected boolean isSuccess(String username, String url) {
		return true;
	}
}
