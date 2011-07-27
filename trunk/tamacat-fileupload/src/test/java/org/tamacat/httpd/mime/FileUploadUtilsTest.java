package org.tamacat.httpd.mime;

import static org.junit.Assert.*;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

public class FileUploadUtilsTest {

	@Test
	public void testFileUploadUtils() {
		new FileUploadUtils();
	}
	
	@Test
	public void testGetFileItemList() {
		HttpContext context = new BasicHttpContext();
		assertNull(FileUploadUtils.getFileItemList(context));
	}

	@Test
	public void testGetException() {
		HttpContext context = new BasicHttpContext();
		FileUploadUtils.getException(context);
	}

}
