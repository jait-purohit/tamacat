/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.net.InetAddress;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;

public class ReverseUtils {

	static final Log LOG = LogFactory.getLog(ReverseUtils.class);
	
	static Charset charset = Charset.forName("UTF-8");
	static CharsetDecoder decoder = charset.newDecoder();
	static CharsetEncoder encoder = charset.newEncoder();

	
	static Pattern PATTERN = Pattern.compile(
		"<[^<]*\\s+(href|src|action)=('|\")([^('|\")]*)('|\")[^>]*>"
	);
	
	//TODO bug?
	public static ByteBuffer parse(ReverseUrl reverseUrl, ByteBuffer buffer) {
		if (reverseUrl == null) return buffer;
		String src = reverseUrl.getPath();
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
	
	static final String REMOTE_ADDRESS = "remote_address";
    
    public static void copyHttpResponse(HttpResponse targetResponse, HttpResponse response) {
        // Remove hop-by-hop headers
        targetResponse.removeHeaders("Content-Length");
        targetResponse.removeHeaders("Transfer-Encoding");
        targetResponse.removeHeaders("Connection");
        targetResponse.removeHeaders("Keep-Alive");
        targetResponse.removeHeaders("TE");
        targetResponse.removeHeaders("Trailers");
        targetResponse.removeHeaders("Upgrade");
        targetResponse.removeHeaders("Content-MD5");
        
//        Header[] setCookies = targetResponse.getHeaders("Set-Cookie");
//        for (Header h : setCookies) {
//        	response.addHeader(h);
//        }
        response.setStatusLine(targetResponse.getStatusLine());
        response.setHeaders(targetResponse.getAllHeaders());
        
        
//        Iterator<String> names = response.getHeaderNames();
//        while (names.hasNext()) {
//        	String name = names.next();
//        	Iterator<String> values = response.getHeaders(name);
//        	while (values.hasNext()) {
//        		String value = values.next();
//        		System.out.println(name+": "+value);
//        		//response.replaceHeader(name, value);
//        	}
//        }
    }
    
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
    
    public static void rewriteSetCookieHeader(HttpResponse response, ReverseUrl reverseUrl) {
        Header[] cookies = response.getHeaders("Set-Cookie");
        ArrayList<String> newValues = new ArrayList<String>();
        for (Header h : cookies) {
        	String value = h.getValue();
        	String newValue = ReverseUtils.getConvertedSetCookieHeader(reverseUrl, value);
        	if (StringUtils.isNotEmpty(newValue)) {
        		newValues.add(newValue);
        	}
        }
        //response.removeHeaders("Set-Cookie"); //TODO Bug.
        for (String newValue : newValues) {
        	response.addHeader("Set-Cookie", newValue);
        }
    }
	
	public static void setRemoteAddress(HttpContext context, InetAddress address) {
		context.setAttribute(REMOTE_ADDRESS, address);
	}
	
	public static String getRemoteIPAddress(HttpContext context) {
		InetAddress address= (InetAddress) context.getAttribute(REMOTE_ADDRESS);
		if (address != null) return address.getHostAddress();
		else return "";
	}
	
	public static String getConvertedSetCookieHeader(ReverseUrl reverseUrl, String line) {
		if (line == null) return "";
		String dist = reverseUrl.getReverse().getHost();
		String src =reverseUrl.getHost().getHost();
		return getConvertedSetCookieHeader(
				reverseUrl.getReverse().getPath(),
				reverseUrl.getPath(),
				line.replace("domain=" + dist, "domain=" + src)
		);
	}
	
	/**
	 * Convert cookie path. 
	 *   BEFORE: JSESSIONID=1234567890ABCDEFGHIJKLMNOPQRSTUV; Path=/dist
	 *   AFTER : JSESSIONID=1234567890ABCDEFGHIJKLMNOPQRSTUV; Path=/src
	 */
	static String getConvertedSetCookieHeader(String dist, String src, String line) {
		if (line != null) {
			String d = stripEnd(dist, "/");
			String s = stripEnd(src, "/");
			// LOG.trace(d +"->" + s);
			return line.replaceAll("; Path=" + d, "; Path=" + s);
		} else {
			return line;
		}
	}
	
	static String stripEnd(String str, String stripChars) {
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
}
