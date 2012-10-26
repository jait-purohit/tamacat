/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.tamacat.util.DateUtils;

public class DynamicRealmTest {
	
	@Test
	public void testRealmDate() {
		Date date = new Date();
		String realm = DynamicRealm.getRealm("Test-${yyyyMMdd}", date);
		Assert.assertEquals("Test-" + DateUtils.getTime(date, "yyyyMMdd"), realm);
	}
}
