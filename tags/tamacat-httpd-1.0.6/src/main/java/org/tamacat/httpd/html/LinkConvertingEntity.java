/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.tamacat.httpd.util.EncodeUtils;
import org.tamacat.httpd.util.HtmlUtils;
import org.tamacat.util.IOUtils;

/**
 * <p>HttpEntity for Link convert.
 */
public class LinkConvertingEntity extends HttpEntityWrapper {

	protected int bufferSize = 8192; //8KB
	protected String before;
	protected String after;
	protected long contentLength = -1;
	protected List<Pattern> linkPatterns;
	protected String defaultCharset = "8859_1";

	public LinkConvertingEntity(HttpEntity entity, String before, String after) {
		this(entity, before, after, HtmlUtils.LINK_PATTERN);
	}
	
	public LinkConvertingEntity(HttpEntity entity, String before, String after, List<Pattern> linkPatterns) {
		super(entity);
		this.before = before;
		this.after = after;
		if (linkPatterns != null && linkPatterns.size() > 0) {
			this.linkPatterns = linkPatterns;
		} else {
			this.linkPatterns = new ArrayList<Pattern>();
			this.linkPatterns.add(HtmlUtils.LINK_PATTERN);
		}
	}
	
	public LinkConvertingEntity(HttpEntity entity, String before, String after, Pattern... linkPattern) {
		super(entity);
		this.before = before;
		this.after = after;
		this.linkPatterns = new ArrayList<Pattern>();
		if (linkPattern != null && linkPattern.length > 0) {
			for (Pattern p : linkPattern) {
				this.linkPatterns.add(p);
			}
		} else {
			this.linkPatterns.add(HtmlUtils.LINK_PATTERN);
		}
	}
	
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
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
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			this.contentLength = wrappedEntity.getContentLength();
			Header contentType = wrappedEntity.getContentType();
			String charset = EncodeUtils.getJavaEncoding(HtmlUtils.getCharSet(contentType));
			if (charset == null) {
				charset = defaultCharset;
			}
			writer = new BufferedWriter(new OutputStreamWriter(outstream, charset));
			reader = new BufferedReader(new InputStreamReader(wrappedEntity.getContent(), charset));

			int length = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				line = line + "\r\n";
				for (Pattern linkPattern : linkPatterns) {
					ConvertData html = HtmlUtils.convertLink(line, before, after, linkPattern);
					if (html.isConverted()) {
						line = html.getData();
					}
				}
				writer.write(line);
				length += line.getBytes(charset).length;
			}
			if (before.length() != after.length()) {
				contentLength = length;
			}
			writer.flush();
		} finally {
			IOUtils.close(reader);
			IOUtils.close(writer);
		}
	}
	
	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}
}
