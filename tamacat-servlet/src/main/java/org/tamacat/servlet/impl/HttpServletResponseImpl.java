package org.tamacat.servlet.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletResponse;
import org.tamacat.servlet.util.ServletUtils;
import org.tamacat.util.StringUtils;

public class HttpServletResponseImpl implements HttpCoreServletResponse {

	protected HttpCoreServletContext servletContext;
	protected HttpResponse response;
	protected HttpContext context;

	protected ServletOutputStream out;
	protected PrintWriter pw;
	protected HttpEntity entity;

	private String characterEncoding;
	private int bufferSize;
	private Locale locale;

	HttpServletResponseImpl(HttpCoreServletContext servletContext,
			HttpResponse response, HttpContext context) {
		this.servletContext = servletContext;
		this.response = response;
		this.context = context;
		this.entity = response.getEntity();
	}

	@Override
	public HttpResponse getHttpResponse() {
		return response;
	}
	
	@Override
	public HttpContext getHttpContext() {
		return context;
	}
	HttpServerConnection getHttpServerConnection() {
    	HttpServerConnection conn = (HttpServerConnection)
		context.getAttribute(ExecutionContext.HTTP_CONNECTION);
    	return conn;
	}
	
	@Override
	public void addCookie(Cookie cookie) {
		if (cookie != null
				&& (cookie.getSecure() == false || cookie.getSecure())) {
			String name = cookie.getName();

			String value = cookie.getValue();
			String domain = cookie.getDomain();
			String path = cookie.getPath();
			int maxAge = cookie.getMaxAge();

			StringBuilder sb = new StringBuilder();
			sb.append(name + "=" + (value != null ? value : ""));

			if (maxAge >= 0) {
				sb.append("; expires=" + (System.currentTimeMillis() + maxAge));
			}
			if (StringUtils.isNotEmpty(domain)) {
				sb.append("; domain=" + domain);
			}
			if (StringUtils.isNotEmpty(path)) {
				sb.append("; path=" + path);
			}
			response.addHeader("Set-Cookie", sb.toString());
		}
	}

	@Override
	public void addDateHeader(String name, long time) {
		response.addHeader(name, ServletUtils.getDate(time));
	}

	@Override
	public void addHeader(String name, String value) {
		response.addHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		response.addHeader(name, String.valueOf(value));
	}

	@Override
	public boolean containsHeader(String name) {
		return response.containsHeader(name);
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int statusCode) throws IOException {
		throw new HttpException(BasicHttpStatus.getHttpStatus(statusCode));
	}

	@Override
	public void sendError(int statusCode, String message) throws IOException {
		throw new HttpException(BasicHttpStatus.getHttpStatus(statusCode), message);
	}

	@Override
	public void sendRedirect(String path) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String name, long time) {
		response.setHeader(name, ServletUtils.getDate(time));
	}

	@Override
	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		response.setHeader(name, String.valueOf(value));
	}

	@Override
	public void setStatus(int statusCode) {
		response.setStatusCode(statusCode);
	}

	@Override
	public void setStatus(int statusCode, String reason) {
		response.setStatusCode(statusCode);
		response.setReasonPhrase(reason);
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public String getCharacterEncoding() {
		if (characterEncoding == null) {
			characterEncoding = "ISO_8859_1";
		}
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		Header header = response.getFirstHeader(HTTP.CONTENT_TYPE);
		return header != null ? header.getValue() : null;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (pw != null) {
			throw new IllegalStateException();
		}
		if (out == null) {
			final ByteArrayOutputStream o = new ByteArrayOutputStream();
			ContentProducer producer = new ContentProducer() {
				public void writeTo(OutputStream out) throws IOException {
					out.write(o.toByteArray());
				}
			};
			HttpEntity entity = new EntityTemplate(producer);
			response.setEntity(entity);
			this.out = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					o.write((int)b);
				}
			};
		}
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (out != null) {
			throw new IllegalStateException();
		}
		if (pw == null) {
			final PrintWriterImpl writer = new PrintWriterImpl(new StringWriter());
			ContentProducer producer = new ContentProducer() {
				public void writeTo(OutputStream out) throws IOException {
					out.write(writer.getWriter().toString().getBytes(getCharacterEncoding()));
				}
			};
			HttpEntity entity = new EntityTemplate(producer);
			response.setEntity(entity);
			pw = writer;
		}
		return pw;
	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {
	}

	@Override
	public void resetBuffer() {
	}

	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	@Override
	public void setContentLength(int length) {
		response.setHeader(HTTP.CONTENT_LEN, String.valueOf(length));
	}

	@Override
	public void setContentType(String value) {
		response.setHeader(HTTP.CONTENT_TYPE, value);
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
