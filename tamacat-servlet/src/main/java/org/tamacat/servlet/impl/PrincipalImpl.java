package org.tamacat.servlet.impl;

import java.security.Principal;

public class PrincipalImpl implements Principal {

	private final String name;
	
	PrincipalImpl(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
