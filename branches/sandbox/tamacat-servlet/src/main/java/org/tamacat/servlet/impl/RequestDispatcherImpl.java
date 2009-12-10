package org.tamacat.servlet.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestDispatcherImpl implements RequestDispatcher {

	protected String path;
	
	RequestDispatcherImpl(String path) {
		this.path = path;
	}
	
	@Override
	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {

	}

	@Override
	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {

	}

}
