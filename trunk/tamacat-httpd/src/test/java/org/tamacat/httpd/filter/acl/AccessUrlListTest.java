package org.tamacat.httpd.filter.acl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccessUrlListTest {

	@Test
	public void testAccessUrlList() {
		AccessUrlList list = new AccessUrlList();
		assertEquals(0, list.size());
	}

}
