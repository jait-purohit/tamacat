/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.util;

import java.util.UUID;

public class UniqueCodeGenerator {

	public static String generate() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static String generate(String prefix) {
		return prefix != null ? prefix + generate() : generate();
	}
}
