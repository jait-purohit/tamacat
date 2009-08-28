/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.junit.Test;

public class DynamicRealmTest {
	
	@Test
	public void testRealm() {
		String realm = DynamicRealm.getRealm("Test-${yyyyMMdd}");
		System.out.println(realm);
	}
}
