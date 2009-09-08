/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.util.IOUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>HTML link convert for reverse proxy.
 */
public class HtmlLinkConvertInterceptor implements HttpResponseInterceptor {

    private Set<String> contentTypes = new HashSet<String>();
    
    public HtmlLinkConvertInterceptor() {
    	contentTypes.add("html");
    }
    
	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        ReverseUrl reverseUrl = (ReverseUrl) context.getAttribute("reverseUrl");
        if (reverseUrl != null) {
	        Header header = response.getFirstHeader(HTTP.CONTENT_TYPE);
	        if (header != null && useLinkConvert(header)) {
	        	String before = reverseUrl.getReverse().getPath();
	        	String after = reverseUrl.getServiceUrl().getPath();
	        	LinkConvertingEntity entity = new LinkConvertingEntity(
	        			response.getEntity(), before, after);
	        	response.setEntity(entity);
	        }
        }
	}

	/**
	 * <p>Set the content type of the link convertion.<br>
	 * default are "text/html" content types to convert.</p>
	 * <p>The {@code contentType} value is case insensitive,<br>
	 * and the white space of before and after is trimmed.</p>
	 * 
	 * <p>Examples: {@code contentType="html, css, javascript, xml" }
	 * <ul>
	 *   <li>text/html</li>
	 *   <li>text/css</li>
	 *   <li>text/javascript</li>
	 *   <li>application/xml</li>
	 *   <li>text/xml</li>
	 * </ul>
	 * @param contentType Comma Separated Value of content-type or sub types.
	 */
	public void setContentType(String contentType) {
		if (StringUtils.isNotEmpty(contentType)) {
			String[] csv = contentType.split(",");
			for (String t : csv) {
				contentTypes.add(t.trim().toLowerCase());
				String[] types = t.split(";")[0].split("/");
				if (types.length >= 2) {
					contentTypes.add(types[1].trim().toLowerCase());
				}
			}
		}
	}
	
	/**
	 * <p>Check for use link convert.
	 * @param contentType
	 * @return true use link convert.
	 */
	boolean useLinkConvert(Header contentType) {
		if (contentType == null) return false;
		String type = contentType.getValue();
		if (contentTypes.contains(type)) {
			return true;
		} else {
			//Get the content sub type. (text/html; charset=UTF-8 -> html)
			String[] types = type != null ? type.split(";")[0].split("/") : new String[0];
			if (types.length >= 2 && contentTypes.contains(types[1])) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * <p>HttpEntity for Link convert.
	 */
	static class LinkConvertingEntity extends HttpEntityWrapper {
		
		static final Pattern PATTERN = Pattern.compile(
			"<[^<]*\\s+(href|src|action)=['|\"]([^('|\")]*)['|\"][^>]*>",
			Pattern.CASE_INSENSITIVE
		);
		private static final int bufferSize = 2048;
		private String before;
		private String after;
		private long contentLength = -1;
		
		public LinkConvertingEntity(HttpEntity entity, String before, String after) {
			super(entity);
	        this.before = before;
	        this.after = after;
	    }
		
		@Override
	    public long getContentLength() {
	        return contentLength;
	    }
		
		@Override
	    public void writeTo(OutputStream outstream) throws IOException {
	        if (outstream == null) {
	            throw new IllegalArgumentException("Output stream may not be null");
	        }
	        BufferedOutputStream out = new BufferedOutputStream(outstream);
	        try {
	        	BufferedInputStream in = new BufferedInputStream(
	        			wrappedEntity.getContent());
	        	byte[] tmp = new byte[bufferSize];
	        	this.contentLength = wrappedEntity.getContentLength();
	        	Header contentType = wrappedEntity.getContentType();
	        	String charset = getJavaEncoding(getCharSet(contentType));
	        	int l;
	        	while ((l = in.read(tmp)) != -1) {
	        		ConvertData html = convert(new String(tmp, charset), before, after);
	        		if (html.isConverted()) {
	        			byte[] bytes = html.getData().getBytes(charset);
	    				int diff = bytes.length - tmp.length;
	        			out.write(bytes, 0, (l + diff));
	        			contentLength += diff;
	        		} else {
	        			out.write(tmp, 0, l);
	        		}
	        	}
	        	out.flush();
	        } finally {
	        	IOUtils.close(out);
	        }
	        //System.out.println("writeTo: " + contentLength); //debug
	    }
		
		static String getCharSet(Header contentType) {
			if (contentType != null) {
				String value = contentType.getValue();
				if (value.indexOf("=") >= 0) {
					String[] values = value.split("=");
					if (values != null && values.length >= 2) {
						String charset = values[1];
						return charset.toLowerCase().trim();
					}
				}
			}
			return null;
		}
		
		static String getJavaEncoding(String encoding) {
			if (encoding == null) return "8859_1";
			if (encoding.startsWith("utf")) {
				return "UTF-8";
			} else if (encoding.startsWith("shift")) {
				return "MS932";
			} else if (encoding.startsWith("euc")) {
				return "EUC_JP";
			} else if (encoding.startsWith("jis")
				|| encoding.startsWith("2022")) {
				return "2022-JP";
			}
			return "8859_1";
		}
		
		static class ConvertData {
			private final boolean converted;
			private final String data;

			public ConvertData(String data, boolean converted) {
				this.data = data;
				this.converted = converted;
			}
			
			public String getData() {
				return data;
			}
			
			public boolean isConverted() {
				return converted;
			}
		}
		
		static ConvertData convert(String html, String before, String after) {
    		Matcher matcher = PATTERN.matcher(html);
    		StringBuffer result = new StringBuffer();
    		boolean converted = false;
    		while (matcher.find()) {
				String url = matcher.group(2);
				if (url.startsWith("http"))	continue;
				String rev = matcher.group().replaceFirst(before, after);
				matcher.appendReplacement(result, rev.replace("$", "\\$"));
				converted = true;
    		}
			matcher.appendTail(result);
			//System.out.println("URLConvert: " + before + " -> " + after); //debug
			return new ConvertData(result.toString(), converted);
		}
	}
}
