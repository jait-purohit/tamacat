/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.core.RequestParameters;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.util.IOUtils;

public class RequestUtils {

	public static final String REMOTE_ADDRESS = "remote_address";

	static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	static final String REQUEST_PARAMETERS_CONTEXT_KEY = "RequestParameters";

	public static String getRequestPath(HttpRequest request) {
		String path = request.getRequestLine().getUri();
		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			return requestParams[0];
		}
		return path;
	}
	
	public static void setParameter(HttpContext context, String name, String... values) throws IOException {
		RequestParameters parameters = getParameters(context);
		parameters.setParameter(name, values);
	}
	
	public static void setParameters(
			HttpRequest request, HttpContext context, String encoding) {
		String path = request.getRequestLine().getUri();
		//String path = docsRoot + request.getRequestLine().getUri();
		RequestParameters parameters = getParameters(context);

		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			path = requestParams[0];
			//set request parameters for Custom HttpRequest.
			if (requestParams.length >= 2) {
				String params = requestParams[1];
				String[] param = params.split("&");
				for (String kv : param) {
					String[] p = kv.split("=");
					if (p.length >=2) {
						parameters.setParameter(p[0], decode(p[1], encoding));
					}
				}
			}
		}
		Header contentType = request.getFirstHeader(HTTP.CONTENT_TYPE);
		if (contentType != null
		  && CONTENT_TYPE_FORM_URLENCODED.equalsIgnoreCase(contentType.getValue())) {
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
				if (entity != null) {
					InputStream in = null;
					try {
						in = entity.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						String s;
						StringBuilder sb = new StringBuilder();
						while ((s = reader.readLine()) != null) {
							sb.append(s);
						}
						String[] params = sb.toString().split("&");
						for (String param : params) {
							String[] keyValue = param.split("=");
							if (keyValue.length >= 2) {
								parameters.setParameter(keyValue[0], 
										decode(keyValue[1], encoding));
							}
						}
					} catch (IOException e) {
						throw new HttpException(BasicHttpStatus.SC_BAD_REQUEST, e);
					} finally {
						IOUtils.close(in);
					}
				}
			}
		}
	}
	
	public static RequestParameters getParameters(HttpContext context) {
		synchronized (context) {
			RequestParameters params = (RequestParameters) context.getAttribute(REQUEST_PARAMETERS_CONTEXT_KEY);
			if (params == null) {
				params = new RequestParameters();
				context.setAttribute(REQUEST_PARAMETERS_CONTEXT_KEY, params);
			}
			return params;
		}
	}
	
	public static String getParameter(HttpContext context, String name) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameter(name) : null;
	}
	
	public static String[] getParameters(HttpContext context, String name) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameters(name) : null;
	}
	
	public static Set<String> getParameterNames(HttpContext context) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameterNames() : null;
	}
	
	public static HttpServerConnection getHttpServerConnection(HttpContext context) {
		return (HttpServerConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
	}
	
	/**
	 * Set the remote IP address to {@code HttpContext}.
	 * @param context
	 * @param conn instance of HttpInetConnection
	 */
	public static void setRemoteAddress(HttpContext context, HttpServerConnection conn) {
		if (conn instanceof HttpInetConnection) {
			InetAddress address = ((HttpInetConnection)conn).getRemoteAddress();
			context.setAttribute(REMOTE_ADDRESS, address);
		}
	}
	
	/**
	 * Get the remote IP address in {@code HttpContext}.
	 * @param context
	 * @return
	 */
	public static String getRemoteIPAddress(HttpContext context) {
		InetAddress address = (InetAddress) context.getAttribute(REMOTE_ADDRESS);
		if (address != null) return address.getHostAddress();
		else return "";
	}

	public static String getRequestHostURL(
			HttpRequest request, HttpContext context) {
		URL host = getRequestURL(request, context);
		return host != null ? host.getProtocol()
				+ "://" + host.getAuthority() : null;
	}
	
	public static String getRequestHostURL(
			HttpRequest request, HttpContext context, ServiceUrl url) {
		URL host = getRequestURL(request, context, url);
		return host != null ? host.getProtocol()
				+ "://" + host.getAuthority() : null;
	}
	
	public static URL getRequestURL(HttpRequest request, HttpContext context) {
		return getRequestURL(request, context, null);
	}
	
	public static URL getRequestURL(HttpRequest request, HttpContext context, ServiceUrl url) {
		String protocol = "http";
		String hostName = null;
		int port = -1;
		Header hostHeader = request.getFirstHeader(HTTP.TARGET_HOST);
		if (hostHeader != null) {
			hostName = hostHeader.getValue();
		}
		if (url != null) {
			URL configureHost = url.getHost();
			if (configureHost != null) {
				protocol = configureHost.getProtocol();
				if (hostName == null) {
					hostName = configureHost.getHost();
				}
			}
			if (url.getServerConfig().useHttps()) {
				protocol = "https";
			}
			port = url.getServerConfig().getPort();
			if (context != null) {
			HttpServerConnection con = getHttpServerConnection(context);
				if (con instanceof HttpInetConnection) {
					port = ((HttpInetConnection)con).getRemotePort();
					InetAddress addr = ((HttpInetConnection)con).getLocalAddress();
					if (hostName == null && addr != null) {
						hostName = addr.getHostName();
					}
				}
			}
		}
		if (("http".equalsIgnoreCase(protocol) && port == 80) 
			|| ("https".equalsIgnoreCase(protocol) && port == 443)){
			port = -1;
		}
		if (hostName != null) {
			try {
				URL hostUrl = new URL(protocol, hostName, port,
						request.getRequestLine().getUri());
//				HttpHost httpHost = new HttpHost(hostName, port);
//				context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, httpHost);
				return hostUrl;
			} catch (MalformedURLException e) {
				//e.printStackTrace();
			}
		}
		return null;
	}
	
	private static String decode(String value, String encoding) {
		String decode = null;
		try {
			decode = URLDecoder.decode(value, encoding);
		} catch (Exception e) {
			decode = value;
		}
		return decode;
	}
}
