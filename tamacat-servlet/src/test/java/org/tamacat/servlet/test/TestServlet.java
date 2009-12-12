package org.tamacat.servlet.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(
		HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		String id = request.getParameter("id");
		System.out.println("execute doGet() id=" + id);
	}
	
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		System.out.println("execute doPost()");
	}
}
