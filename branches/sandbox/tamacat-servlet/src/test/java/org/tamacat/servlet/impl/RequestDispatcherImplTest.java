package org.tamacat.servlet.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestDispatcherImplTest {

	HttpServletRequestImpl request;
	RequestDispatcherImpl dispatcher;
	
	@Before
	public void setUp() throws Exception {
		//req = new BasicHttpRequest("GET", "/test/index.html?id=amdin&type=user");

		dispatcher = new RequestDispatcherImpl("/test/");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testForward() throws Exception {
		dispatcher.forward(request, null);
	}

	@Test
	public void testInclude() {
		fail("Not yet implemented");
	}

}
