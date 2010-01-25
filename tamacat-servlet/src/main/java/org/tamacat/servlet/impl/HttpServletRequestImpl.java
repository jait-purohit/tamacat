package org.tamacat.servlet.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.tamacat.util.StringUtils;

public class HttpServletRequestImpl implements HttpCoreServletRequest {

	private static final String JSESSIONID = "JSESSIONID";
	
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
	private String serverName;
	private int serverPort = -1;
	private String sessionId;
	private boolean isRequestedSessionIdFromCookie;
	private ServletUrl servletUrl;
	private Principal principal;
	
	public HttpServletRequestImpl(
			HttpCoreServletContext servletContext,
			ServletUrl servletUrl,
			HttpRequest request, HttpContext context) {
		this.servletContext = servletContext;
		this.servletUrl = servletUrl;
		this.request = request;
		this.context = context;
		RequestUtils.setParameters(request, context);
	}
	
	@Override
	public HttpRequest getHttpRequest() {
		return request;
	}
	
	@Override
	public HttpContext getHttpContext() {
		return context;
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
		return servletContext.getServiceUrl().getPath().replaceFirst("/$", "");
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
		String uri = request.getRequestLine().getUri();
		if (uri != null && uri.indexOf('?') >= 0) {
			uri = uri.split("\\?")[0];
		}
		return uri;
	}

	@Override
	public StringBuffer getRequestURL() {
		StringBuffer url = new StringBuffer();
        String scheme = getScheme();
        int port = getServerPort();
        url.append(scheme + "://" + getServerName());
        if (port > 0 &&
        	    ("http".equalsIgnoreCase(scheme) && port != 80) || 
                ("https".equalsIgnoreCase(scheme) && port != 443)) {
            url.append(":" + port);
        }
        url.append(getRequestURI());
        return url;
	}

	@Override
	public String getRequestedSessionId() {
		if (StringUtils.isNotEmpty(sessionId)) return sessionId;
		HttpSession session = getSession(false);
		return session != null ? session.getId() : sessionId;
	}

	@Override
	public String getServletPath() {
		return servletUrl.getServletPath(getRequestURI());
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (sessionId == null) {
			Cookie[] cookies = getCookies();
			for (Cookie cookie : cookies) {
				if (JSESSIONID.equalsIgnoreCase(cookie.getName())) {
					sessionId = cookie.getValue();
					break;
				}
			}
			if (StringUtils.isNotEmpty(sessionId)) {
				isRequestedSessionIdFromCookie = true;
			}
		}
		HttpSession session = HttpSessionFacade.getInstance(servletContext)
			.getSession(sessionId, create);
		if (session != null) {
			sessionId = session.getId();
		}
		return session;
	}

	@Override
	public Principal getUserPrincipal() {
		if (principal == null) {
			String remoteUser = (String) context.getAttribute(AuthComponent.REMOTE_USER_KEY);
			if (StringUtils.isNotEmpty(remoteUser)) {
				principal = new PrincipalImpl(remoteUser);
			}
		}
		return principal;
	}
	
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		if (isRequestedSessionIdFromCookie) {
			return isRequestedSessionIdFromCookie;
		}
		Cookie[] cookies = getCookies();
		for (Cookie cookie : cookies) {
			if (JSESSIONID.equalsIgnoreCase(cookie.getName())) {
				sessionId = cookie.getValue();
				break;
			}
		}
		if (StringUtils.isNotEmpty(sessionId)) {
			isRequestedSessionIdFromCookie = true;
			return true;
		}
		return isRequestedSessionIdFromCookie;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return ! isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return getSession(false) != null;
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

	private HttpInetConnection getHttpInetConnection() {
		HttpServerConnection conn = getHttpServerConnection();
    	if (conn instanceof HttpInetConnection) {
			return (HttpInetConnection)conn;
		}
    	return null;
	}
	
	private HttpServerConnection getHttpServerConnection() {
    	HttpServerConnection conn = (HttpServerConnection)
		context.getAttribute(ExecutionContext.HTTP_CONNECTION);
    	return conn;
	}
	
	@Override
	public String getLocalAddr() {
		HttpInetConnection conn = getHttpInetConnection();
    	if (conn != null) {
			return conn.getLocalAddress().getHostAddress();
		}
		return null;
	}

	@Override
	public String getLocalName() {
		HttpInetConnection conn = getHttpInetConnection();
		if (conn != null) {
			return conn.getLocalAddress().getHostName();
		}
		return null;
	}

	@Override
	public int getLocalPort() {
		HttpInetConnection conn = getHttpInetConnection();
		if (conn != null) {
			return conn.getLocalPort();
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
		return servletContext.getRealPath(path);
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
		if (serverName != null) {
			return serverName;
		}
		Header host = request.getFirstHeader("Host");
		if (host != null) {
			serverName = host.getValue();
		} else {
			HttpInetConnection conn = getHttpInetConnection();
			if (conn != null) {
				serverName = conn.getLocalAddress().getHostName();
			}
			if (serverName == null) {
				try {
					serverName = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
				}
			}
		}
		return serverName;
	}

	@Override
	public int getServerPort() {
		HttpInetConnection conn = getHttpInetConnection();
		if (serverPort == -1 && conn != null) {
			serverPort = conn.getLocalPort();	
		} else {
			serverPort = servletContext.getServiceUrl().getServerConfig().getPort();
		}
		return serverPort;
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
