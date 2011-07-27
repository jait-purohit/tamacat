package org.tamacat.httpd.mime;

import static org.junit.Assert.*;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HTTP;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpFileUploadTest {
	
	HttpFileUpload upload;
	
	@Before
	public void setUp() throws Exception {
		upload = new HttpFileUpload();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAlgorithm() {
		assertEquals("SHA-256", upload.getAlgorithm());
		
		upload.setAlgorithm("MD5");
		assertEquals("MD5", upload.getAlgorithm());
	}

	
	
	@Test
	public void testParseRequestHttpRequest() throws Exception {
		HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", "/test.html");
		request.setEntity(new StringEntity(""));
		String CONTENT_TYPE = "multipart/form-data; boundary=---1234";
		request.addHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE);
		upload.parseRequest(request);
	}

	@Test
	public void testParseRequestRequestContext() throws Exception {
		HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", "/test.html");
		request.setEntity(new StringEntity(""));
		String CONTENT_TYPE = "multipart/form-data; boundary=---1234";
		request.addHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE);
		
		HttpRequestContext ctx = new HttpRequestContext(request);
		try {
			upload.parseRequest(ctx);
		} catch (NullPointerException e) {
			
		}
		upload.setFileItemFactory(new DiskFileItemFactory());
		upload.parseRequest(ctx);
	}

}
