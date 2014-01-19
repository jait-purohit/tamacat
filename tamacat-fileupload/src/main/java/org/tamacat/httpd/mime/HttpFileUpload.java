/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.mime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.HttpRequest;
import org.tamacat.io.MessageDigestInputStream;

public class HttpFileUpload extends FileUpload {

	private String algorithm = "SHA-256";

	/**
	 * <p>Get the algorithm of checksum. (MessageDigest)
	 * Default algorithm: "SHA-256"
	 * @return algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * <p>Set the algorithm of checksum. (MessageDigest)
	 * Default algorithm: "SHA-256"
	 * @param algorithm
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public List<FileItem> parseRequest(HttpRequest request)
			throws FileUploadException {
		RequestContext ctx = new HttpRequestContext(request);
		if (getFileItemFactory() == null) {
			setFileItemFactory(new DiskFileItemFactory());
		}
		return parseRequest(ctx);
	}

	@Override
	public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
	try {
		FileItemIterator iter = getItemIterator(ctx);
		List<FileItem> items = new ArrayList<FileItem>();
		FileItemFactory fac = getFileItemFactory();
		if (fac == null) {
			throw new NullPointerException(
				"No FileItemFactory has been set.");
		}
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			HttpFileItem fileItem = new HttpFileItem(fac.createItem(item.getFieldName(),
					item.getContentType(), item.isFormField(),
					item.getName())
			);
			try {
				MessageDigestInputStream in = new MessageDigestInputStream(
						item.openStream(), algorithm);
				Streams.copy(in, fileItem.getOutputStream(), true);

				fileItem.setDigest(in.getDigest());
			} catch (FileUploadIOException e) {
				throw (FileUploadException) e.getCause();
			} catch (IOException e) {
				throw new IOFileUploadException(
						"Processing of " + MULTIPART_FORM_DATA
						+ " request failed. " + e.getMessage(), e);
			}
			items.add(fileItem);
		}
		return items;
	} catch (FileUploadIOException e) {
		throw (FileUploadException) e.getCause();
	} catch (IOException e) {
		throw new FileUploadException(e.getMessage(), e);
	}
	}
}
