/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.ResourceNotFoundException;
import org.tamacat.util.StringUtils;

/**
 * <p>The utility class for reverse proxy.<br>
 *  When customize a request/response header to remove in reverse
 *  to the origin server, create the "reverse-header.properties" in CLASSPATH.
 * <pre>Configuration: reverse-header.properties
 * {@code
 * request.removeHeaders=Content-Length,Transfer-Encoding,Accept-Encoding,...
 * response.removeHeaders=Content-Type,Content-Encoding,Content-Length,...
 * }</pre>
 */
public class ReverseUtils {

	static final Log LOG = LogFactory.getLog(ReverseUtils.class);
	
	private static Charset charset = Charset.forName("UTF-8");
	private static CharsetDecoder decoder = charset.newDecoder();
	private static CharsetEncoder encoder = charset.newEncoder();
	
	private static Pattern PATTERN = Pattern.compile(
		"<[^<]*\\s+(href|src|action)=('|\")([^('|\")]*)('|\")[^>]*>"
	);
	
	private static final String HEADER_PROPERTIES = "reverse-header.properties";
	private static final String DEFAULT_HEADER_PROPERTIES = "org/tamacat/httpd/util/reverse-header.properties";
	
	private static final Set<String> removeRequestHeaders = new HashSet<String>();
	private static final Set<String> removeResponseHeaders = new HashSet<String>();

	//Configuration of remove request/response headers.
	static {
		Properties props = null;
		try {
			props = PropertyUtils.getProperties(HEADER_PROPERTIES);
		} catch (ResourceNotFoundException e) {
			props = PropertyUtils.getProperties(DEFAULT_HEADER_PROPERTIES);
		}
		if (props != null) {
			String removeHeaders1 = props.getProperty("request.removeHeaders");
			String[] headers1 = removeHeaders1.split(",");
			for (String h : headers1) {
				removeRequestHeaders.add(h.trim());
			}
			String removeHeaders2 = props.getProperty("response.removeHeaders");
			String[]headers2 = removeHeaders2.split(",");
			for (String h : headers2) {
				removeResponseHeaders.add(h.trim());
			}
		}
	}
	
	/**
	 * <p>Remove hop-by-hop headers.
	 * @param request
	 */
	public static void removeRequestHeaders(HttpRequest request) {
		for (String h : removeRequestHeaders) {
			request.removeHeaders(h);
		}
	}
	
	/**
	 * <p>Copy the response headers.
	 * @param targetResponse
	 * @param response
	 */
    public static void copyHttpResponse(HttpResponse targetResponse, HttpResponse response) {
        // Remove hop-by-hop headers
    	for (String h : removeResponseHeaders) {
    		targetResponse.removeHeaders(h);
    	}
        
        response.setStatusLine(targetResponse.getStatusLine());
        Header[] headers = response.getHeaders("Set-Cookie"); //backup Set-Cookie header.
        response.setHeaders(targetResponse.getAllHeaders()); //clean and reset all headers.
        for (Header h : headers) { //add Set-Cookie headers.
        	response.addHeader(h);
        }
    }
    
    /**
     * <p>Rewrite the Content-Location response headers.
     * @param response
     * @param reverseUrl
     */
    public static void rewriteContentLocationHeader(HttpResponse response, ReverseUrl reverseUrl) {
        Header[] locationHeaders = response.getHeaders("Content-Location");
        response.removeHeaders("Content-Location");
        for (Header location : locationHeaders) {
        	String value = location.getValue().replace("\r", "").replace("\n","");
        	String convertUrl = reverseUrl.getConvertRequestedUrl(value);
        	if (convertUrl != null) {
        		response.addHeader("Content-Location", convertUrl);
        	}
        }
    }
    
    /**
     * <p>Rewrite the Location response headers.
     * @param response
     * @param reverseUrl
     */
    public static void rewriteLocationHeader(HttpResponse response, ReverseUrl reverseUrl) {
    	Header[] locationHeaders = response.getHeaders("Location");
    	response.removeHeaders("Location");
        for (Header location : locationHeaders) {
        	String value = location.getValue().replace("\r", "").replace("\n","");
        	String convertUrl = reverseUrl.getConvertRequestedUrl(value);
        	if (convertUrl != null) {
        		response.addHeader("Location", convertUrl);
        	}
        }
    }
    
    /**
     * <p>Rewrite the Set-Cookie response headers.
     * @param response
     * @param reverseUrl
     */
    public static void rewriteSetCookieHeader(HttpResponse response, ReverseUrl reverseUrl) {
        Header[] cookies = response.getHeaders("Set-Cookie");
        ArrayList<String> newValues = new ArrayList<String>();
        for (Header h : cookies) {
        	String value = h.getValue();
        	String newValue = ReverseUtils.getConvertedSetCookieHeader(reverseUrl, value);
        	if (StringUtils.isNotEmpty(newValue)) {
        		newValues.add(newValue);
            	response.removeHeader(h);
        	}
        }
        for (String newValue : newValues) {
        	response.addHeader("Set-Cookie", newValue);
        }
    }
    
    /**
     * <p>Set the remote IP address to {@code X-Forwarded-For} request header
     * for origin server.
     * @param request
     * @param context
     */
    public static void setXForwardedFor(HttpRequest request, HttpContext context) {
    	request.setHeader("X-Forwarded-For", AccessLogUtils.getRemoteIPAddress(context));
    }
	
	/**
	 * <p>Convert backend hostname to original hostname.
	 * @param reverseUrl
	 * @param line cookie header line.
	 * @return converted Set-Cookie response header line.
	 */
	public static String getConvertedSetCookieHeader(ReverseUrl reverseUrl, String line) {
		if (line == null) return "";
		String dist = reverseUrl.getReverse().getHost();
		String src =reverseUrl.getHost().getHost();
		return getConvertedSetCookieHeader(
				reverseUrl.getReverse().getPath(),
				reverseUrl.getServiceUrl().getPath(),
				line.replace("domain=" + dist, "domain=" + src)
		);
	}
	
	/**
	 * <p>Get the Cookie value in request headers.
	 * @param request
	 * @param name
	 * @return Cookie value.
	 */
	public static String getCookieValue(HttpRequest request, String name) {
		Header[] headers = request.getHeaders("Cookie");
		for (Header h : headers) {
			String value = h.getValue();
			StringTokenizer st = new StringTokenizer(value, ";");
			if (st.countTokens() == 0) continue;
			while (st.hasMoreTokens()) {
				String set = st.nextToken().trim();
				String[] nameValue = set.split("=");
				if (nameValue.length < 2) continue;
				if (nameValue[0].equals(name)) {
					return nameValue[1];
				}
			}
		}
		return null;
	}
	
	/**
	 * <p>Convert cookie path. 
	 * <pre>
	 *   BEFORE: JSESSIONID=1234567890ABCDEFGHIJKLMNOPQRSTUV; Path=/dist
	 *   AFTER : JSESSIONID=1234567890ABCDEFGHIJKLMNOPQRSTUV; Path=/src
	 * </pre>
	 */
	private static String getConvertedSetCookieHeader(String dist, String src, String line) {
		if (line != null) {
			String d = stripEnd(dist, "/");
			String s = stripEnd(src, "/");
			// LOG.trace(d +"->" + s);
			return line.replaceAll("; Path=" + d, "; Path=" + s);
		} else {
			return line;
		}
	}
	
	private static String stripEnd(String str, String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}
		if (stripChars == null) {
			while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
				end--;
			}
		} else if (stripChars.length() == 0) {
			return str;
		} else {
			while ((end != 0)
					&& (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
				end--;
			}
		}
		return str.substring(0, end);
	}
	
	//TODO bug?
	public static ByteBuffer parse(ReverseUrl reverseUrl, ByteBuffer buffer) {
		if (reverseUrl == null) return buffer;
		String src = reverseUrl.getServiceUrl().getPath();
		String dist = reverseUrl.getReverse().getPath();
		ByteBuffer result = null;
    	try {
    		CharBuffer cb = decoder.decode(buffer);
    		Matcher matcher = PATTERN.matcher(cb);
    		StringBuffer tmp = new StringBuffer();
    		while (matcher.find()) {
				String url = matcher.group(3);
				if (url.startsWith("http"))	continue;
				LOG.trace("URL:" + url);
				// LOG.trace(dist +"->" + src);
				String rev = matcher.group().replaceFirst(dist, src);
				LOG.trace("->URL:" + rev);
				matcher.appendReplacement(tmp, rev.replace("$", "\\$"));
    		}
			matcher.appendTail(tmp);
			LOG.trace("URLConvert: " + dist + " -> " + src);
			cb = CharBuffer.wrap(tmp.toString());
			result = encoder.encode(cb);
		} catch (CharacterCodingException e) {
			result = buffer;
		}
		return result;
	}
}