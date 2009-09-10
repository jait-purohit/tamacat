/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.html;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.tamacat.httpd.util.EncodeUtils;
import org.tamacat.util.IOUtils;

/**
 * <p>HttpEntity for Link convert.
 */
public class LinkConvertingEntity extends HttpEntityWrapper {

	static final Pattern PATTERN = Pattern.compile(
			"<[^<]*\\s+(href|src|action)=['|\"]([^('|\")]*)['|\"][^>]*>",
			Pattern.CASE_INSENSITIVE);

	static final Pattern CHARSET_PATTERN = Pattern.compile(
			"<meta[^<]*\\s+(content)=(.*);\\s(charset)=(.*)['|\"][^>]*>",
			Pattern.CASE_INSENSITIVE);

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
			BufferedInputStream in = new BufferedInputStream(wrappedEntity
					.getContent());
			byte[] tmp = new byte[bufferSize];
			this.contentLength = wrappedEntity.getContentLength();
			Header contentType = wrappedEntity.getContentType();
			String charset = EncodeUtils.getJavaEncoding(getCharSet(contentType));
			int l;
			while ((l = in.read(tmp)) != -1) {
				if (charset == null) {
					charset = getCharSetFromMetaTag(new String(tmp, "8859_1"),
							"UTF-8");
				}
				ConvertData html = convert(
						new String(tmp, charset), before, after);
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

	static String getCharSetFromMetaTag(String html, String defaultCharset) {
		if (html != null) {
			Matcher matcher = CHARSET_PATTERN.matcher(html);
			if (matcher.find()) {
				String charset = matcher.group(4);
				return charset != null ? charset.toLowerCase().trim()
						: defaultCharset;
			}
		}
		return defaultCharset;
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
			if (url.startsWith("http"))
				continue;
			String rev = matcher.group().replaceFirst(before, after);
			matcher.appendReplacement(result, rev.replace("$", "\\$"));
			converted = true;
		}
		matcher.appendTail(result);
		// System.out.println("URLConvert: " + before + " -> " + after); //debug
		return new ConvertData(result.toString(), converted);
	}
}
