/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.html;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
			BufferedInputStream in = new BufferedInputStream(wrappedEntity.getContent());
			byte[] tmp = new byte[bufferSize];
			this.contentLength = wrappedEntity.getContentLength();
			Header contentType = wrappedEntity.getContentType();
			String charset = EncodeUtils.getJavaEncoding(HtmlUtils.getCharSet(contentType));
			int l;
			while ((l = in.read(tmp)) != -1) {
				if (charset == null) {
					charset = HtmlUtils.getCharSetFromMetaTag(
							new String(tmp, "8859_1"), "UTF-8");
				}
				ConvertData html = HtmlUtils.convertLink(
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
}
