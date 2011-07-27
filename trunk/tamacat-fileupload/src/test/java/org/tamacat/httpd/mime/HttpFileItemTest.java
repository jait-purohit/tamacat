package org.tamacat.httpd.mime;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.junit.Before;
import org.junit.Test;

public class HttpFileItemTest {

	HttpFileItem item;

	@Before
	public void setUp() throws Exception {
		FileItem item = new DiskFileItemFactory().createItem("test", "text/plain", false, "test.txt");
		this.item = new HttpFileItem(item);
		this.item.getOutputStream();
	}
	
	@Test
	public void testGetDigest() {
		item.setDigest("1234567890");
		assertEquals("1234567890", item.getDigest());
	}

	@Test
	public void testGetInputStream() throws Exception {
		assertNotNull(item.getInputStream());
	}

	@Test
	public void testGetContentType() {
		assertEquals("text/plain", item.getContentType());
	}

	@Test
	public void testGetName() {
		assertEquals("test.txt", item.getName());
	}

	@Test
	public void testIsInMemory() throws Exception {
		assertEquals(true, item.isInMemory());
	}

	@Test
	public void testGetSize() {
		item.getSize();
	}

	@Test
	public void testGet() {
		item.get();
	}

	@Test
	public void testGetStringString() throws Exception {
		item.getString("UTF-8");
	}

	@Test
	public void testGetString() {
		item.getString();
	}

	@Test
	public void testWrite() throws Exception {
		File file = new File("test.txt");
		item.write(file);
	}

	@Test
	public void testDelete() {
		item.delete();
	}

	@Test
	public void testGetFieldName() {
		assertEquals("test", item.getFieldName());
		
		item.setFieldName("test2");
		assertEquals("test2", item.getFieldName());
	}

	@Test
	public void testIsFormField() {
		assertEquals(false, item.isFormField());
	}

	@Test
	public void testGetHeaders() {
		item.getHeaders();
	}

}
