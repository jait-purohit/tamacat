package org.tamacat.servlet.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.core.RequestParameters;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.servlet.HttpCoreServletContext;
import org.tamacat.servlet.HttpCoreServletRequest;
import org.tamacat.servlet.util.ServletUtils;

public class HttpServletRequestImpl implements HttpCoreServletRequest {

	protected HttpCoreServletContext servletContext;
	protected HttpRequest request;
	protected HttpContext context;
	
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected ServletInputStream in;
	protected BufferedReader reader;
	
	private String characterEncoding;
	private boolean usedInputStream;
	private boolean usedReader;
	private Set<String> roles = new HashSet<String>();
	
	public HttpServletRequestImpl(
			HttpCoreServletContext servletContext,
			HttpRequest request, HttpContext context) {
		this.servletContext = servletContext;
		this.request = request;
		this.context = context;
		RequestUtils.setParameters(request, context);
	}
	
	@Override
	public void setParameter(String name, String... values) {
		RequestUtils.getParameters(context).setParameter(name, values);
	}
	
	@Override
	public void addUserInRole(String role) {
		roles.add(role);
	}
	
  //--- servlet-api-2.5 ---//
	
	@Override
	public String getAuthType() {
		String header = getHeader("Authorization");
		if (header != null) {
			String[] authTypeValues = header.split(" ");
			if (authTypeValues != null && authTypeValues.length >= 1) {
				String authType = authTypeValues[0];
				if ("Basic".equalsIgnoreCase(authType)) {
					return HttpServletRequest.BASIC_AUTH;
				} else if ("Digest".equalsIgnoreCase(authType)) {
					return HttpServletRequest.DIGEST_AUTH;
//				} else if ("Form".equalsIgnoreCase(authType)) {
//					return HttpServletRequest.FORM_AUTH;
//				} else if ("Client_Cert".equalsIgnoreCase(authType)) {
//					return HttpServletRequest.CLIENT_CERT_AUTH;
				}
			}
		}
		return null;
	}

	@Override
	public String getContextPath() {
		return servletContext.getServiceUrl().getPath();
	}

	@Override
	public Cookie[] getCookies() {
		return ServletUtils.getCookies(request);
	}

	@Override
	public long getDateHeader(String name) {
		Header header = request.getFirstHeader(name);
		if (header != null) {
			return ServletUtils.getTime(header.getValue());
		}
		return -1;
	}

	@Override
	public String getHeader(String name) {
		Header header = request.getFirstHeader(name);
		return header != null ? header.getValue() : null;
	}

	@Override
	public Enumeration<?> getHeaderNames() {
		final Header[] headers = request.getAllHeaders();
		//return Collections.enumeration(Arrays.asList(headers));
		return new Enumeration<String>() {
			int index = 0;
			@Override
			public boolean hasMoreElements() {
				return headers.length >= index;
			}

			@Override
			public String nextElement() {
				Header h = headers[index];
				index++;
				return h.getName();
			}
		};
	}

	@Override
	public Enumeration<?> getHeaders(String name) {
		final Header[] headers = request.getHeaders(name);
		//return Collections.enumeration(Arrays.asList(headers));
		return new Enumeration<String>() {
			int index = 0;
			@Override
			public boolean hasMoreElements() {
				return headers.length >= index;
			}

			@Override
			public String nextElement() {
				Header h = headers[index];
				index++;
				return h.getValue();
			}
		};
	}

	@Override
	public int getIntHeader(String name) {
		String value = getHeader(name);
		if (value != null) {
			return Integer.parseInt(value);
		} else {
			return -1;
		}
	}

	@Override
	public String getMethod() {
		return request.getRequestLine().getMethod();
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		return servletContext.getRealPath(getPathInfo());
	}

	@Override
	public String getQueryString() {
		return request.getRequestLine().getUri();
	}

	@Override
	public String getRemoteUser() {
		return (String) context.getAttribute(AuthComponent.REMOTE_USER_KEY);
	}

	@Override
	public String getRequestURI() {
		return request.getRequestLine().getUri();
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (getRemoteUser() == null) return false;
		return roles.contains(role);
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		//536373 nano sec.
		return Collections.enumeration(attributes.keySet());
//		//1118428 nano sec.
//		final String[] keys = attributes.keySet().toArray(new String[attributes.size()]);
//		return new Enumeration<String>() {
//			int index = 0;
//			@Override
//			public boolean hasMoreElements() {
//				return keys.length >= index;
//			}
//
//			@Override
//			public String nextElement() {
//				String n = keys[index];
//				index++;
//				return n;
//			}
//		};
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public int getContentLength() {
		return -1;
	}

	@Override
	public String getContentType() {
		Header header = request.getFirstHeader(HTTP.CONTENT_TYPE);
		return header != null ? header.getValue() : null;
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (usedReader) throw new IllegalStateException();
		if (in == null && request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
			in = new ServletInputStreamImpl(entity.getContent());
			usedInputStream = true;
		}
		return in;
	}

	@Override
	public String getLocalAddr() {
    	HttpServerConnection conn = (HttpServerConnection)
				context.getAttribute(ExecutionContext.HTTP_CONNECTION);
		if (conn instanceof HttpInetConnection) {
			return ((HttpInetConnection)conn).getLocalAddress().getHostAddress();
		}
		return null;
	}

	@Override
	public String getLocalName() {
    	HttpServerConnection conn = (HttpServerConnection)
				context.getAttribute(ExecutionContext.HTTP_CONNECTION);
		if (conn instanceof HttpInetConnection) {
			return ((HttpInetConnection)conn).getLocalAddress().getHostName();
		}
		return null;
	}

	@Override
	public int getLocalPort() {
    	HttpServerConnection conn = (HttpServerConnection)
    			context.getAttribute(ExecutionContext.HTTP_CONNECTION);
		if (conn instanceof HttpInetConnection) {
			return ((HttpInetConnection)conn).getLocalPort();
		}
		return -1;
	}

	@Override
	public Locale getLocale() {
		String header = getHeader("Accept-Language");
		return header != null ? new Locale(header) : null;
	}

	@Override
	public Enumeration<?> getLocales() {
		Header[] headers = request.getHeaders("Accept-Language");
		Set<Locale> locales = new LinkedHashSet<Locale>();
		if (headers != null && headers.length > 0) {
			for (Header h : headers) {
				String value = h.getValue();
				if (value != null && value.length() > 0) {
					locales.add(new Locale(value));
				}
			}
		}
		return Collections.enumeration(locales);
	}

	@Override
	public String getParameter(String name) {
		return RequestUtils.getParameter(context, name);
	}

	@Override
	public Map<?,?> getParameterMap() {
		RequestParameters params = RequestUtils.getParameters(context);
		Map<String, List<String>> map = params.getParameterMap();
		Map<String, String[]> remake = new LinkedHashMap<String, String[]>();
		String[] keys = map.keySet().toArray(new String[map.keySet().size()]);
		for (int i=0; i<keys.length; i++) {
			String name = keys[i];
			List<String> values = map.get(name);
			remake.put(name, values.toArray(new String[values.size()]));
		}
		return remake;
	}

	@Override
	public Enumeration<?> getParameterNames() {
		return Collections.enumeration(RequestUtils.getParameterNames(context));
	}

	@Override
	public String[] getParameterValues(String name) {
		return RequestUtils.getParameters(context, name);
	}

	@Override
	public String getProtocol() {
		return request.getRequestLine().getProtocolVersion().toString();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (usedInputStream) throw new IllegalStateException();
		if (reader == null && request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
			reader = new BufferedReader(new InputStreamReader(
							entity.getContent(), getCharacterEncoding()));
			usedReader = true;
		}
		return reader;
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		InetAddress address = (InetAddress) context.getAttribute("remote_address");
		return address != null ? address.getHostAddress() : null;
	}

	@Override
	public String getRemoteHost() {
		InetAddress address = (InetAddress) context.getAttribute("remote_address");
		return address != null ? address.getHostName() : null;
	}

	@Override
	public int getRemotePort() {
        HttpServerConnection conn = (HttpServerConnection)
        		context.getAttribute(ExecutionContext.HTTP_CONNECTION);
		if (conn instanceof HttpInetConnection) {
			return ((HttpInetConnection)conn).getRemotePort();
		}
		return -1;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return new RequestDispatcherImpl(path);
	}

	@Override
	public String getScheme() {
		return servletContext.getServiceUrl().getHost().getProtocol();
	}

	@Override
	public String getServerName() {
		return servletContext.getServiceUrl().getServerConfig().getParam("ServerName");
	}

	@Override
	public int getServerPort() {
		return servletContext.getServiceUrl().getServerConfig().getPort();
	}

	@Override
	public boolean isSecure() {
		return "true".equalsIgnoreCase(
			servletContext.getServiceUrl().getServerConfig().getParam("https"));
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	public void setCharacterEncoding(String characterEncoding)
			throws UnsupportedEncodingException {
		this.characterEncoding = characterEncoding;
	}
}
