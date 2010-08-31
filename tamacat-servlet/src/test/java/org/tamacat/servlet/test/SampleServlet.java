package org.tamacat.servlet.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.util.StringUtils;

public class SampleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(
		HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		
		String id = request.getParameter("id");
		//System.out.println("execute doGet() id=" + id);

		String method = request.getParameter("method");
		if (StringUtils.isEmpty(method)) method = "get";
		
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		out.println("execute doGet() id=" + id + "<br />");
		out.println("<form method='" + method + "' action='" + request.getRequestURI() + "'>");
		out.println("<input type='text' name='id' value='" + id + "' />");
		out.println("<input type='submit' value='OK' />");
		out.println("</form>");
		out.println("</body></html>");

	}
	
	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String id = request.getParameter("id");
		//System.out.println("execute doPost() id=" + id);

		ServletOutputStream out = response.getOutputStream();
		out.println("<html><body>");
		out.println("execute doPost() id=" + id);
		out.println("</body></html>");
	}
	
	@Override
    public String getServletInfo() {
    	return "TestServlet";
    }
}
