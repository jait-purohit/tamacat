package org.tamacat.servlet.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;

public class ServletUrl {

	private String servletName;
	private String urlPattern;
	private HttpServlet servlet;
	
	public String getServletPath(String url) {
		String pattern = "(" + urlPattern + ")";
		boolean wildcard = false;
		if (urlPattern.indexOf('*') >= 0) {
			wildcard = true;
		}
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(url);
		while (matcher.find()) {
			String result = matcher.group(0);
			if (wildcard) {
				int end = url.indexOf(result) + result.length();
				result = url.substring(0, end);
			}
			return result;
		}
		return null;
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}
	
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	public String getServletName() {
		return servletName;
	}
	
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public HttpServlet getServlet() {
		return servlet;
	}
	
	public void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}
}
